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
import cc.mallet.topics.DMRTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.ReadableDocument;
import uk.ac.ed.inf.ace.Task;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DMR extends ConfiguredMalletClassifierBase<uk.ac.ed.inf.setmac.config.v1.Dmr> {

  private class EmpiricalLikelihoodDistance implements InstanceModelDistance {

    final DMRTopicModel topicModel;
    final Pipe pipe;

    private EmpiricalLikelihoodDistance(DMRTopicModel topicModel, Pipe pipe) {
      this.topicModel = topicModel;
      this.pipe = pipe;
    }

    @Override
    public double getDistance(Instance instance) {
      InstanceList instanceList = new InstanceList(pipe);
      instanceList.add(instance);
      return -topicModel.empiricalLikelihood(numSamples, instanceList);
    }
  }

  private class MalletTrainer extends MalletTrainerBase {

    private final Set<Object> labels;
    private final DMRTopicModel topicModel;

    private MalletTrainer(Random random, Set<Object> labels) {
      super(new SerialPipes(new Pipe[]{
            new Target2Label(), // Target String -> class label
            new TokenSequence2FeatureSequence(), // Replace each Token with a feature index
          }));
      this.labels = labels;
      this.topicModel = new DMRTopicModel(getNumTopics());
      this.topicModel.setRandomSeed(random.nextInt());
      this.topicModel.setTopicDisplay(getShowTopicsInterval(), getTopWords());
      this.topicModel.setNumIterations(getNumIterations());
      this.topicModel.setOptimizeInterval(getOptimizeInterval());
      this.topicModel.setBurninPeriod(getOptimizeBurnIn());
    }

    @Override
    public Model createModel() throws Exception {
      Map<Object, InstanceList> instanceLists = getInstanceLists();
      Builder<Object, InstanceModelDistance> instanceModelDistancesBuilder = ImmutableMap.builder();

      for (Object label : labels) {
        topicModel.addInstances(instanceLists.get(label));
        instanceModelDistancesBuilder.put(label,
            new EmpiricalLikelihoodDistance(topicModel, getPipe()));
      }

      topicModel.estimate();
      TopicModelClassifier topicModelClassifier;

      switch (getClassifierType()) {
        case MinimumDistance:
          topicModelClassifier = new MinimumDistanceClassifier();
          break;
        case GaussianDistance:
          topicModelClassifier = new GaussianDistanceClassifier(
              instanceModelDistancesBuilder.build(), instanceLists);
          break;
        default:
          throw new Exception("Unknown distance based classifier type encountered: "
              + getClassifierType());
      }

      return new MalletModel(getPipe(), labels, topicModel, topicModelClassifier);
    }
  }

  private class MalletModel implements Model {

    private final Pipe pipe;
    private final Set<Object> labels;
    private final DMRTopicModel topicModel;
    private final TopicModelClassifier classifier;

    private MalletModel(Pipe pipe, Set<Object> labels, DMRTopicModel topicModel,
        TopicModelClassifier classifier) {
      this.pipe = pipe;
      this.labels = labels;
      this.topicModel = topicModel;
      this.classifier = classifier;
    }

    @Override
    public Object classify(ReadableDocument document, Object actualLabel, Task task) throws Exception {
      Builder<Object, Double> distancesBuilder = ImmutableMap.builder();

      for (Object label : labels) {
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(getInstance(document, label));
        double distance = -topicModel.empiricalLikelihood(numSamples, instanceList);
        distancesBuilder.put(label, distance);
      }

      return classifier.classify(distancesBuilder.build());
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {

      try (PrintWriter printWriter = new PrintWriter(new File(outputDirectory,
              outputFilePrefix + "TopicReport.xml"))) {
        topicModel.topicXMLReport(printWriter, getTopWords());
        printWriter.close();

        try (PrintStream printStream = new PrintStream(new File(outputDirectory,
                outputFilePrefix + "TopicPhraseReport.xml"))) {
          topicModel.topicXMLReportPhrases(printStream, getTopWords());
        }
      }
    }
  }
  private final int numSamples;

  public DMR(Engine engine, uk.ac.ed.inf.setmac.config.v1.Dmr config) {
    super(engine, config);
    numSamples = config.getNumSamples();
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new MalletTrainer(randomSource.get(this), labels);
  }
}