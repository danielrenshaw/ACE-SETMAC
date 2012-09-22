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

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Map.Entry;
import uk.ac.ed.inf.ace.utils.Utilities;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class ConfiguredMalletClassifierBase<C extends uk.ac.ed.inf.setmac.config.v1.MalletClassifierBase>
    extends MalletClassifierBase<C> {

  protected static enum ClassifierType {

    Svm,
    MinimumDistance,
    GaussianDistance
  }

  protected static interface InstanceModelDistance {

    double getDistance(Instance instance);
  }

  protected static interface TopicModelClassifier {

    Object classify(Map<Object, Double> distances);
  }

  protected static class MinimumDistanceClassifier implements TopicModelClassifier {

    @Override
    public Object classify(Map<Object, Double> distances) {
      double minDistance = Double.POSITIVE_INFINITY;
      Object minLabel = null;

      for (Entry<Object, Double> entry : distances.entrySet()) {
        double distance = entry.getValue();

        if (distance < minDistance) {
          minDistance = distance;
          minLabel = entry.getKey();
        }
      }

      return minLabel;
    }
  }

  protected static class GaussianDistanceClassifier implements TopicModelClassifier {

    private final Map<Object, Double> distanceVariances;

    protected GaussianDistanceClassifier(Map<Object, InstanceModelDistance> instanceModelDistances,
        Map<Object, InstanceList> instanceLists) {
      Builder<Object, Double> distanceVariancesBuilder = ImmutableMap.builder();

      for (Entry<Object, InstanceList> entry : instanceLists.entrySet()) {
        Object label = entry.getKey();
        InstanceList instanceList = entry.getValue();
        double[] distances = new double[instanceList.size()];

        for (int index = 0; index < distances.length; index++) {
          double distance = instanceModelDistances.get(label).getDistance(instanceList.get(index));
          distances[index] = distance;
        }

        double variance = 0.0;

        for (int index = 0; index < distances.length; index++) {
          variance += Math.pow(distances[index], 2);
        }

        distanceVariancesBuilder.put(label, variance);
      }

      this.distanceVariances = distanceVariancesBuilder.build();
    }

    @Override
    public Object classify(Map<Object, Double> distances) {
      double maxProbability = Double.NEGATIVE_INFINITY;
      Object maxLabel = null;

      for (Entry<Object, Double> entry : distances.entrySet()) {
        Object label = entry.getKey();
        double variance = Utilities.ifNull(distanceVariances.get(label), Double.MIN_VALUE);
        double probability = Math.exp(-Math.pow(entry.getValue(), 2) / (2 * variance))
            / Math.sqrt(variance);

        if (probability > maxProbability) {
          maxProbability = probability;
          maxLabel = label;
        }
      }

      return maxLabel;
    }
  }
  private final int numTopics;
  private final int showTopicsInterval;
  private final int topWords;
  private final int numIterations;
  private final int optimizeInterval;
  private final int optimizeBurnIn;
  private final ClassifierType classifierType;

  protected ConfiguredMalletClassifierBase(Engine engine, C config) {
    super(engine, config);
    numTopics = config.getNumTopics();
    showTopicsInterval = config.getShowTopicsInterval();
    topWords = config.getTopWords();
    numIterations = config.getNumIterations();
    optimizeInterval = config.getOptimizeInterval();
    optimizeBurnIn = config.getOptimizeBurnIn();
    this.classifierType = ClassifierType.valueOf(
        config.getClassifierType());
  }

  protected int getNumIterations() {
    return numIterations;
  }

  protected int getNumTopics() {
    return numTopics;
  }

  protected int getOptimizeBurnIn() {
    return optimizeBurnIn;
  }

  protected int getOptimizeInterval() {
    return optimizeInterval;
  }

  protected int getShowTopicsInterval() {
    return showTopicsInterval;
  }

  protected int getTopWords() {
    return topWords;
  }

  protected ClassifierType getClassifierType() {
    return classifierType;
  }
}