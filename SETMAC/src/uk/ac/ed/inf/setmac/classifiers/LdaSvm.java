/*
 * Copyright 2012 Daniel Renshaw &lt;d.renshaw@sms.ed.ac.uk&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ed.inf.setmac.classifiers;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import libsvm.*;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.ace.utils.Utilities;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class LdaSvm extends ConfiguredMalletClassifierBase<uk.ac.ed.inf.setmac.config.v1.LdaSvm> {

  private class MalletTrainer extends MalletTrainerBase {

    private final Random random;
    private final Map<Object, ParallelTopicModel> topicModels;
    private final Map<Object, Integer> labelIndex;

    private MalletTrainer(Random random, Set<Object> labels) {
      super(new SerialPipes(new Pipe[]{
            new Target2Label(), // Target String -> class label
            new TokenSequence2FeatureSequence(), // Replace each Token with a feature index
          }));
      this.random = random;
      Builder<Object, ParallelTopicModel> topicModelsBuilder = ImmutableMap.builder();
      Builder<Object, Integer> labelIndexBuilder = ImmutableMap.builder();
      int nextLabelIndex = 0;

      for (Object label : labels) {
        topicModelsBuilder.put(label, createParallelTopicModel());
        labelIndexBuilder.put(label, nextLabelIndex);
        nextLabelIndex++;
      }

      this.topicModels = topicModelsBuilder.build();
      this.labelIndex = labelIndexBuilder.build();
    }

    private ParallelTopicModel createParallelTopicModel() {
      ParallelTopicModel topicModel = new ParallelTopicModel(getNumTopics(), alpha, beta);
      topicModel.setRandomSeed(random.nextInt());
      topicModel.setTopicDisplay(getShowTopicsInterval(), getTopWords());
      topicModel.setNumIterations(getNumIterations());
      topicModel.setOptimizeInterval(getOptimizeInterval());
      topicModel.setBurninPeriod(getOptimizeBurnIn());
      topicModel.setSymmetricAlpha(useSymmetricAlpha);
      topicModel.setNumThreads(numThreads);
      topicModel.printLogLikelihood = printLogLikelihood;
      return topicModel;
    }

    @Override
    public Model createModel() throws Exception {
      Map<Object, InstanceList> instanceLists = getInstanceLists();
      Pipe pipe = getPipe();
      int numInstances = 0;

      // Train topic models
      for (Entry<Object, ParallelTopicModel> entry : topicModels.entrySet()) {
        Object label = entry.getKey();
        ParallelTopicModel topicModel = entry.getValue();
        InstanceList instanceList = instanceLists.get(label);
        numInstances += instanceList.size();
        topicModel.addInstances(Utilities.ifNull(instanceList, new InstanceList(pipe)));
        topicModel.estimate();
      }

      // Train SVM
      svm_parameter param = new svm_parameter();

      switch (svmType) {
        case CSvc:
          param.svm_type = svm_parameter.C_SVC;
          break;
        case NuSvc:
          param.svm_type = svm_parameter.NU_SVC;
          break;
        default:
          throw new Exception("Unknown svmType encountered: " + svmType);
      }

      switch (svmKernelType) {
        case Linear:
          param.kernel_type = svm_parameter.LINEAR;
          break;
        case Polynomial:
          param.kernel_type = svm_parameter.POLY;
          break;
        case RBF:
          param.kernel_type = svm_parameter.RBF;
          break;
        case Sigmoid:
          param.kernel_type = svm_parameter.SIGMOID;
          break;
        default:
          throw new Exception("Unknown svmKernelType encountered: " + svmKernelType);
      }

      param.degree = svmDegree;
      param.gamma = svmGamma;
      param.coef0 = svmCoef0;
      param.nu = svmNu;
      param.cache_size = svmCacheSize;
      param.C = svmCost;
      param.eps = svmTerminationToleranceEpsilon;
      param.p = svmLossFunctionEpsilon;
      param.shrinking = svmShrinking ? 1 : 0;
      param.probability = svmProbabilityEstimates ? 1 : 0;
      param.nr_weight = 0;
      param.weight_label = new int[0];
      param.weight = new double[0];

      svm_problem prob = new svm_problem();
      prob.l = numInstances;
      prob.x = new svm_node[numInstances][];
      prob.y = new double[numInstances];
      int instanceIndex = 0;

      for (Entry<Object, InstanceList> entry : instanceLists.entrySet()) {
        for (Instance instance : entry.getValue()) {
          prob.x[instanceIndex] = getNodes(topicModels, labelIndex, instance);
          prob.y[instanceIndex] = labelIndex.get(entry.getKey());
          instanceIndex++;
        }
      }

      svm_model svmModel = svm.svm_train(prob, param);
      return new LdaMalletModel(topicModels, labelIndex, svmModel, getPipe());
    }
  }

  private svm_node[] getNodes(Map<Object, ParallelTopicModel> topicModels,
      Map<Object, Integer> labelIndex, Instance instance) {
    int numTopics = getNumTopics();
    svm_node[] nodes = new svm_node[topicModels.size() * numTopics];

    for (Entry<Object, ParallelTopicModel> entry : topicModels.entrySet()) {
      double[] features = entry.getValue().getInferencer().getSampledDistribution(instance,
          numInferencerIterations, inferencerThinning, inferencerBurnIn);

      for (int featureIndex = 0; featureIndex < features.length; featureIndex++) {
        svm_node node = new svm_node();
        node.index = featureIndex + 1;
        node.value = features[featureIndex];
        nodes[labelIndex.get(entry.getKey()) * numTopics + featureIndex] = node;
      }
    }

    return nodes;
  }

  private class LdaMalletModel extends MalletModelBase {

    private final Map<Object, ParallelTopicModel> topicModels;
    private final Map<Object, Integer> labelIndex;
    private final Map<Integer, Object> indexLabel;
    private final svm_model svmModel;

    private LdaMalletModel(Map<Object, ParallelTopicModel> topicModels,
        Map<Object, Integer> labelIndex, svm_model svmModel, Pipe pipe) {
      super(pipe);
      this.topicModels = topicModels;
      this.labelIndex = labelIndex;
      this.svmModel = svmModel;
      Builder<Integer, Object> builder = ImmutableMap.builder();

      for (Entry<Object, Integer> entry : labelIndex.entrySet()) {
        builder.put(entry.getValue(), entry.getKey());
      }

      this.indexLabel = builder.build();
    }

    @Override
    protected Object classify(Instance instance) {
      svm_node[] x = getNodes(topicModels, labelIndex, instance);
      double y = svm.svm_predict(svmModel, x);
      return indexLabel.get((int) y);
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {
      for (Entry<Object, ParallelTopicModel> entry : this.topicModels.entrySet()) {
        Object label = entry.getKey();
        ParallelTopicModel topicModel = entry.getValue();

        try (PrintWriter printWriter = new PrintWriter(new File(outputDirectory,
                outputFilePrefix + label + "-TopicReport.xml"))) {
          topicModel.topicXMLReport(printWriter, getTopWords());
        }

        try (PrintWriter printWriter = new PrintWriter(new File(outputDirectory,
                outputFilePrefix + label + "-TopicPhraseReport.xml"))) {
          topicModel.topicPhraseXMLReport(printWriter, getTopWords());
        }
      }
    }
  }

  private enum SvmType {

    CSvc,
    NuSvc
  }

  private enum SvmKernelType {

    Linear,
    Polynomial,
    RBF,
    Sigmoid
  }
  private final double alpha;
  private final double beta;
  private final boolean useSymmetricAlpha;
  private final int numThreads;
  private final boolean printLogLikelihood;
  private final int numInferencerIterations;
  private final int inferencerThinning;
  private final int inferencerBurnIn;
  private final SvmType svmType;
  private final SvmKernelType svmKernelType;
  private final int svmDegree;
  private final double svmGamma;
  private final double svmCoef0;
  private final double svmCost;
  private final double svmNu;
  private final double svmLossFunctionEpsilon;
  private final int svmCacheSize;
  private final double svmTerminationToleranceEpsilon;
  private final boolean svmShrinking;
  private final boolean svmProbabilityEstimates;

  public LdaSvm(Engine engine, uk.ac.ed.inf.setmac.config.v1.LdaSvm config) {
    super(engine, config);
    this.alpha = config.getAlpha().doubleValue();
    this.beta = config.getBeta().doubleValue();
    this.useSymmetricAlpha = config.isUseSymmetricAlpha();
    this.numThreads = config.getNumThreads();
    this.printLogLikelihood = config.isPrintLogLikelihood();
    this.numInferencerIterations = config.getNumInferencerIterations();
    this.inferencerThinning = config.getInferencerThinning();
    this.inferencerBurnIn = config.getInferencerBurnIn();
    this.svmType = Enum.valueOf(SvmType.class, config.getSvmType());
    this.svmKernelType = Enum.valueOf(SvmKernelType.class, config.getSvmKernelType());
    this.svmDegree = config.getSvmDegree();
    this.svmGamma = config.getSvmGamma().doubleValue();
    this.svmCoef0 = config.getSvmCoef0().doubleValue();
    this.svmCost = config.getSvmCost().doubleValue();
    this.svmNu = config.getSvmNu().doubleValue();
    this.svmLossFunctionEpsilon = config.getSvmLossFunctionEpsilon().doubleValue();
    this.svmCacheSize = config.getSvmCacheSize();
    this.svmTerminationToleranceEpsilon = config.getSvmTerminationToleranceEpsilon().doubleValue();
    this.svmShrinking = config.isSvmShrinking();
    this.svmProbabilityEstimates = config.isSvmProbabilityEstimates();
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new MalletTrainer(randomSource.get(this), labels);
  }
}