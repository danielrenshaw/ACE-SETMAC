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
import cc.mallet.topics.MarginalProbEstimator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Maths;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.ace.utils.Utilities;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class LDA extends ConfiguredMalletClassifierBase<uk.ac.ed.inf.setmac.config.v1.Lda> {

  private static enum InstanceModelDistanceType {

    None,
    LeftToRight,
    MeanTopics
  }

  private class LeftToRightDistance implements InstanceModelDistance {

    final MarginalProbEstimator marginalProbEstimator;
    final Pipe pipe;

    private LeftToRightDistance(ParallelTopicModel topicModel, Pipe pipe) {
      this.marginalProbEstimator = topicModel.getProbEstimator();
      this.pipe = pipe;
    }

    @Override
    public double getDistance(Instance instance) {
      InstanceList instanceList = new InstanceList(pipe);
      instanceList.add(instance);
      return -marginalProbEstimator.evaluateLeftToRight(instanceList, numEstimatorParticles,
          estimatorUsingResampling, null);
      //return 1 - Math.exp(marginalProbEstimator.evaluateLeftToRight(instanceList,
      //numEstimatorParticles, estimatorUsingResampling, null));
    }
  }

  private class MeanTopicDistance implements InstanceModelDistance {

    final TopicInferencer topicInferencer;
    final double[] meanTopicProbabilities;

    private MeanTopicDistance(ParallelTopicModel topicModel, Random random) {
      this.topicInferencer = topicModel.getInferencer();
      this.topicInferencer.setRandomSeed(random.nextInt());
      this.meanTopicProbabilities = new double[topicModel.getTopicAlphabet().size()];
      int instanceCount = topicModel.getData().size();

      for (int instanceId = 0; instanceId < instanceCount; instanceId++) {
        double[] topicProbabilities = topicModel.getTopicProbabilities(instanceId);

        for (int index = 0; index < this.meanTopicProbabilities.length; index++) {
          this.meanTopicProbabilities[index] += topicProbabilities[index];
        }
      }

      for (int index = 0; index < this.meanTopicProbabilities.length; index++) {
        this.meanTopicProbabilities[index] /= instanceCount;
      }
    }

    @Override
    public double getDistance(Instance instance) {
      double[] topicProbabilities = topicInferencer.getSampledDistribution(instance,
          numInferencerIterations, inferencerThinning, inferencerBurnIn);
      return Math.sqrt(Maths.jensenShannonDivergence(meanTopicProbabilities, topicProbabilities));
    }
  }

  private class MalletTrainer extends MalletTrainerBase {

    private final Random random;
    private final Map<Object, ParallelTopicModel> topicModels;

    private MalletTrainer(Random random, Set<Object> labels) {
      super(new SerialPipes(new Pipe[]{
            new Target2Label(), // Target String -> class label
            new TokenSequence2FeatureSequence(), // Replace each Token with a feature index
          }));
      this.random = random;
      Builder<Object, ParallelTopicModel> builder = ImmutableMap.builder();

      for (Object label : labels) {
        builder.put(label, createParallelTopicModel());
      }

      this.topicModels = builder.build();
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
      Builder<Object, InstanceModelDistance> instanceModelDistancesBuilder = ImmutableMap.builder();
      Pipe pipe = getPipe();

      for (Entry<Object, ParallelTopicModel> entry : topicModels.entrySet()) {
        Object label = entry.getKey();
        ParallelTopicModel topicModel = entry.getValue();
        topicModel.addInstances(Utilities.ifNull(instanceLists.get(label), new InstanceList(pipe)));
        topicModel.estimate();

        switch (instanceModelDistanceType) {
          case LeftToRight:
            instanceModelDistancesBuilder.put(label, new LeftToRightDistance(topicModel, pipe));
            break;
          case MeanTopics:
            instanceModelDistancesBuilder.put(label, new MeanTopicDistance(topicModel, random));
            break;
          default:
            throw new Exception(
                "Unknown instance-model distance type encountered: " + instanceModelDistanceType);
        }
      }

      TopicModelClassifier topicModelClassifier;
      Map<Object, InstanceModelDistance> instanceModelDistances =
          instanceModelDistancesBuilder.build();

      switch (getClassifierType()) {
        case MinimumDistance:
          topicModelClassifier = new MinimumDistanceClassifier();
          break;
        case GaussianDistance:
          topicModelClassifier = new GaussianDistanceClassifier(instanceModelDistances,
              instanceLists);
          break;
        default:
          throw new Exception("Unknown distance based classifier type encountered: "
              + getClassifierType());
      }

      return new LdaMalletModel(topicModels, instanceModelDistances, topicModelClassifier,
          getPipe());
    }
  }

  private class LdaMalletModel extends MalletModelBase {

    private final Map<Object, ParallelTopicModel> topicModels;
    private final Map<Object, InstanceModelDistance> instanceModelDistances;
    private final TopicModelClassifier classifier;

    private LdaMalletModel(Map<Object, ParallelTopicModel> topicModels,
        Map<Object, InstanceModelDistance> instanceModelDistances,
        TopicModelClassifier classifier, Pipe pipe) {
      super(pipe);
      this.topicModels = topicModels;
      this.instanceModelDistances = instanceModelDistances;
      this.classifier = classifier;
    }

    @Override
    protected Object classify(Instance instance) {
      Builder<Object, Double> distancesBuilder = ImmutableMap.builder();

      for (Entry<Object, InstanceModelDistance> entry : instanceModelDistances.entrySet()) {
        distancesBuilder.put(entry.getKey(), entry.getValue().getDistance(instance));
      }

      return classifier.classify(distancesBuilder.build());
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
  private final double alpha;
  private final double beta;
  private final boolean useSymmetricAlpha;
  private final int numThreads;
  private final boolean printLogLikelihood;
  private final int numEstimatorParticles;
  private final boolean estimatorUsingResampling;
  private final int numInferencerIterations;
  private final int inferencerThinning;
  private final int inferencerBurnIn;
  private final InstanceModelDistanceType instanceModelDistanceType;

  public LDA(Engine engine, uk.ac.ed.inf.setmac.config.v1.Lda config) {
    super(engine, config);
    this.alpha = config.getAlpha().doubleValue();
    this.beta = config.getBeta().doubleValue();
    this.useSymmetricAlpha = config.isUseSymmetricAlpha();
    this.numThreads = config.getNumThreads();
    this.printLogLikelihood = config.isPrintLogLikelihood();
    this.numEstimatorParticles = config.getNumEstimatorParticles();
    this.estimatorUsingResampling = config.isEstimatorUsingResampling();
    this.numInferencerIterations = config.getNumInferencerIterations();
    this.inferencerThinning = config.getInferencerThinning();
    this.inferencerBurnIn = config.getInferencerBurnIn();
    this.instanceModelDistanceType = InstanceModelDistanceType.valueOf(
        config.getInstanceModelDistanceType());
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new MalletTrainer(randomSource.get(this), labels);
  }
}