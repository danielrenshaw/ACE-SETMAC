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
package uk.ac.ed.inf.setmac.databases;

import cc.mallet.types.Dirichlet;
import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Generator {

  private final long randomSeed;
  private final int rowCount;
  private final String[] vocabulary;
  private final double[][] topicVocabularyDistributions;
  private final double[][] topicStatesDistribution;
  private final int minLength;
  private final double lengthVariance;
  private final double lengthMean;
  private final Dirichlet dirichlet;

  public Generator(long randomSeed, int rowCount, String[] vocabulary,
      double[][] topicVocabularyDistributions, double[][] topicStatesDistribution, int minLength,
      double lengthVariance, double lengthMean, double alpha) throws Exception {
    this.randomSeed = randomSeed;
    this.rowCount = rowCount;
    this.vocabulary = vocabulary;
    int topicCount = topicVocabularyDistributions.length;
    assert topicStatesDistribution.length == topicCount;
    this.topicVocabularyDistributions = new double[topicCount][];
    this.topicStatesDistribution = new double[topicCount][];

    for (int index = 0; index < topicCount; index++) {
      this.topicVocabularyDistributions[index] = toCumulativeDistribution(
          topicVocabularyDistributions[index]);
      this.topicStatesDistribution[index] = toCumulativeDistribution(
          topicStatesDistribution[index]);
    }

    this.minLength = minLength;
    this.lengthVariance = lengthVariance;
    this.lengthMean = lengthMean;
    this.dirichlet = new Dirichlet(topicCount, alpha);
  }

  private static double[] toCumulativeDistribution(double[] distribution) throws Exception {
    double total = 0.0;
    double[] cumulativeDistribution = new double[distribution.length];

    for (int index = 0; index < distribution.length; index++) {
      total += distribution[index];

      if (Math.abs(total - 1.0) < 0.000000001) {
        total = 1.0;
      }

      cumulativeDistribution[index] = total;
    }

    if (total != 1.0) {
      throw new Exception("Not a valid distribtion, total = " + total);
    }

    return cumulativeDistribution;
  }

  private static int getIndex(double[] distribution, double value) {
    int index = 0;
    double total = distribution[0];

    while (value > total && index < (distribution.length - 1)) {
      total += distribution[++index];
    }

    return index;
  }

  public void write(File directory) throws Exception {
    PrintWriter postsPrintWriter = null;
    PrintWriter postHistoryPrintWriter = null;

    try {
      postsPrintWriter = new PrintWriter(new File(directory, "posts.xml"), "UTF-8");
      postHistoryPrintWriter = new PrintWriter(new File(directory, "posthistory.xml"), "UTF-8");

      postsPrintWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      postHistoryPrintWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

      postsPrintWriter.println("<posts>");
      postHistoryPrintWriter.println("<posthistory>");

      Random random = new Random(randomSeed);

      for (int index = 1; index <= rowCount; index++) {
        double[] topicDistribution = toCumulativeDistribution(dirichlet.nextDistribution());
        writePost(postsPrintWriter, random, index, topicDistribution);
        writePostHistory(postHistoryPrintWriter, random, index, topicDistribution);
      }

      postsPrintWriter.print("</posts>");
      postHistoryPrintWriter.print("</posthistory>");
    } finally {
      if (postsPrintWriter != null) {
        postsPrintWriter.close();
      }

      if (postHistoryPrintWriter != null) {
        postHistoryPrintWriter.close();
      }
    }
  }

  private void writePost(PrintWriter postsPrintWriter, Random random, int index,
      double[] topicDistribution) {
    double length = Math.max(minLength, random.nextGaussian() * lengthVariance + lengthMean);
    StringBuilder stringBuilder = new StringBuilder();

    for (int wordIndex = 0; wordIndex < length; wordIndex++) {
      int topic = getIndex(topicDistribution, random.nextDouble());
      double[] vocabularyDistribution = topicVocabularyDistributions[topic];
      int word = getIndex(vocabularyDistribution, random.nextInt(vocabularyDistribution.length));
      stringBuilder.append(vocabulary[word]);
      stringBuilder.append(" ");
    }

    postsPrintWriter.format("  <row Id=\"%d\" PostTypeId=\"1\" "
        + "CreationDate=\"2001-01-01T00:00:00.000\" Score=\"0\" Body=\"%s\" "
        + "LastActivityDate=\"2001-01-01T00:00:00.000\" />\n", index, stringBuilder);
  }

  private void writePostHistory(PrintWriter postHistoryPrintWriter, Random random, int index,
      double[] topicDistribution) {
    int topic = getIndex(topicDistribution, random.nextDouble());
    double[] statesDistribution = topicStatesDistribution[topic];
    double stateProbability = random.nextDouble();
    int postHistoryTypeId;
    String closeReasonIdAttribute;

    if (stateProbability < statesDistribution[0]) {
      postHistoryTypeId = 11;
      closeReasonIdAttribute = "";
    } else {
      int closeReasonId;

      if (stateProbability < statesDistribution[1]) {
        closeReasonId = 2;
      } else if (stateProbability < statesDistribution[2]) {
        closeReasonId = 3;
      } else if (stateProbability < statesDistribution[3]) {
        closeReasonId = 4;
      } else {
        closeReasonId = 7;
      }

      postHistoryTypeId = 10;
      closeReasonIdAttribute = String.format("CloseReasonId=\"%d\" ", closeReasonId);
    }

    postHistoryPrintWriter.format("  <row Id=\"%d\" PostHistoryTypeId=\"%d\" PostId=\"%d\" "
        + "RevisionGUID=\"00000000-0000-0000-0000-000000000001\" "
        + "CreationDate=\"2001-01-01T00:00:00.000\" %s/>\n", index, postHistoryTypeId, index,
        closeReasonIdAttribute);
  }
}