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
import cc.mallet.topics.SimpleLDA;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.util.Maths;
import cc.mallet.util.Randoms;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class LDAAlt extends MalletClassifierBase<uk.ac.ed.inf.setmac.config.v1.SimpleLda> {

  private static class SimpleLDAWrapper extends SimpleLDA {

    private SimpleLDAWrapper(LabelAlphabet topicAlphabet, double alphaSum, double beta,
        Randoms random) {
      super(topicAlphabet, alphaSum, beta, random);
    }

    private SimpleLDAWrapper(int numberOfTopics, double alphaSum, double beta, Randoms random) {
      super(numberOfTopics, alphaSum, beta, random);
    }

    private SimpleLDAWrapper(int numberOfTopics, double alphaSum, double beta) {
      super(numberOfTopics, alphaSum, beta);
    }

    private SimpleLDAWrapper(int numberOfTopics) {
      super(numberOfTopics);
    }

    public double getAlpha() {
      return alpha;
    }

    public double getBeta() {
      return beta;
    }

    public double getBetaSum() {
      return betaSum;
    }
  }

  private class MalletTrainer extends MalletTrainerBase {

    private final Random random;
    private final Map<Object, SimpleLDAWrapper> topicModels;

    private MalletTrainer(Random random, Set<Object> labels) {
      super(new SerialPipes(new Pipe[]{
            new Target2Label(), // Target String -> class label
            new TokenSequence2FeatureSequence(), // Replace each Token with a feature index
          }));
      this.random = random;
      Builder<Object, SimpleLDAWrapper> builder = ImmutableMap.builder();

      for (Object label : labels) {
        builder.put(label, createTopicModel());
      }

      topicModels = builder.build();
    }

    private SimpleLDAWrapper createTopicModel() {
      SimpleLDAWrapper topicModel = new SimpleLDAWrapper(numTopics, alpha, beta);
      topicModel.setRandomSeed(random.nextInt());
      topicModel.setTopicDisplay(showTopicsInterval, topWords);
      return topicModel;
    }

    @Override
    public Model createModel() throws Exception {
      for (Entry<Object, SimpleLDAWrapper> entry : topicModels.entrySet()) {
        SimpleLDAWrapper topicModel = entry.getValue();
        topicModel.addInstances(getInstanceLists().get(entry.getKey()));
        topicModel.sample(numIterations);
      }

      //return new MalletModelByLeftToRight(getInstanceList().getPipe(), topicModels);
      return new MalletModelByMeanTopics(getPipe(), topicModels, random);
    }
  }

  private abstract class LdaMalletModelBase extends MalletModelBase {

    private final Map<Object, SimpleLDAWrapper> topicModels;

    private LdaMalletModelBase(Pipe pipe, Map<Object, SimpleLDAWrapper> topicModels) {
      super(pipe);
      this.topicModels = topicModels;
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {
      for (Entry<Object, SimpleLDAWrapper> entry : this.topicModels.entrySet()) {
        Object label = entry.getKey();

        try (PrintStream printStream = new PrintStream(new File(outputDirectory,
                outputFilePrefix + label + "-State.xml"))) {
          entry.getValue().printState(printStream);
        }
      }
    }
  }

  private class MalletModelByMeanTopics extends LdaMalletModelBase {

    private final Map<Object, double[]> meanTopicProbabilities;
    private final Map<Object, TopicInferencer> topicInferencers;

    private MalletModelByMeanTopics(Pipe pipe, Map<Object, SimpleLDAWrapper> topicModels,
        Random random) {
      super(pipe, topicModels);
      Builder<Object, TopicInferencer> topicInferencersBuilder = ImmutableMap.builder();
      Builder<Object, double[]> meanTopicProbabilitiesBuilder = ImmutableMap.builder();

      for (Object label : topicModels.keySet()) {
        SimpleLDAWrapper topicModel = topicModels.get(label);
        int numTopics = topicModel.getNumTopics();
        double[] temp = new double[numTopics];
        double[] alphas = new double[numTopics];
        int[] topicTotals = topicModel.getTopicTotals();
        double total = 0;

        for (int index = 0; index < temp.length; index++) {
          alphas[index] = topicModel.getAlpha();
          double topicTotal = (double) topicTotals[index];
          total += topicTotal;
          temp[index] += topicTotal;
        }

        for (int index = 0; index < temp.length; index++) {
          temp[index] /= total;
        }

        meanTopicProbabilitiesBuilder.put(label, temp);
        TopicInferencer topicInferencer = new TopicInferencer(topicModel.getTypeTopicCounts(),
            topicModel.getTopicTotals(), topicModel.getAlphabet(), alphas, topicModel.getBeta(),
            topicModel.getBetaSum());
        topicInferencer.setRandomSeed(random.nextInt());
        topicInferencersBuilder.put(label, topicInferencer);
      }

      this.topicInferencers = topicInferencersBuilder.build();
      this.meanTopicProbabilities = meanTopicProbabilitiesBuilder.build();
    }

    @Override
    protected Object classify(Instance instance) {
      double minDivergence = Double.POSITIVE_INFINITY;
      Object minLabel = null;

      for (Map.Entry<Object, TopicInferencer> entry : topicInferencers.entrySet()) {
        Object label = entry.getKey();
        double[] topicProbabilities = entry.getValue().getSampledDistribution(instance,
            numInferencerIterations, inferencerThinning, inferencerBurnIn);
        double divergence = Maths.klDivergence(meanTopicProbabilities.get(label),
            topicProbabilities);

        if (divergence < minDivergence) {
          minDivergence = divergence;
          minLabel = label;
        }
      }

      return minLabel;
    }
  }
  private final int numTopics;
  private final double alpha;
  private final double beta;
  private final int showTopicsInterval;
  private final int topWords;
  private final int numIterations;
  private final int numInferencerIterations;
  private final int inferencerThinning;
  private final int inferencerBurnIn;

  public LDAAlt(Engine engine, uk.ac.ed.inf.setmac.config.v1.SimpleLda config) {
    super(engine, config);
    this.numTopics = config.getNumTopics();
    this.alpha = config.getAlpha().doubleValue();
    this.beta = config.getBeta().doubleValue();
    this.showTopicsInterval = config.getShowTopicsInterval();
    this.topWords = config.getTopWords();
    this.numIterations = config.getNumIterations();
    this.numInferencerIterations = config.getNumInferencerIterations();
    this.inferencerThinning = config.getInferencerThinning();
    this.inferencerBurnIn = config.getInferencerBurnIn();
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new MalletTrainer(randomSource.get(this), labels);
  }
}