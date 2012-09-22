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
package uk.ac.ed.inf.setmac.datasets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import uk.ac.ed.inf.ace.*;
import uk.ac.ed.inf.ace.utils.*;
import uk.ac.ed.inf.setmac.Engine;
import uk.ac.ed.inf.setmac.Site;
import uk.ac.ed.inf.setmac.databases.DB;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class SiteBasedDataSet extends DataSetBase<Engine, uk.ac.ed.inf.setmac.config.v1.DataSet> {

  private class SiteBasedSamplingIterator extends SamplingIterator<String> {

    private SiteBasedSamplingIterator(Iterator<String> source, long randomSeed,
        double sampleProbability) {
      super(source, randomSeed, sampleProbability);
    }

    @Override
    protected double getItemSampleProbability(String item) {
      String[] parts = item.split("-");
      return getSiteDetails().get(Integer.parseInt(parts[0]));
    }
  }
  private final HardCache hardCache = new HardCache("SiteBasedDataSet");
  private final SupplierEx<List<Site>> sitesSupplier =
      new SupplierEx<List<Site>>() {
        @Override
        public List<Site> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getSiteOrSiteRef(), getEngine(),
              Site.class, getEngine().getSites());
        }
      };
  private final SupplierEx<Map<Integer, Double>> siteDetailsSupplier =
      new SupplierEx<Map<Integer, Double>>() {
        @Override
        public Map<Integer, Double> get() throws Exception {
          Builder<Integer, Double> builder = ImmutableMap.builder();
          Database database = getEngine().getEnvironment().getDatabase();

          for (Site site : getSites()) {
            builder.put(DB.getSiteId(database, site.getName()),
                site.getSampleProbability());
          }

          return builder.build();
        }
      };

  public SiteBasedDataSet(Engine engine, uk.ac.ed.inf.setmac.config.v1.DataSet config)
      throws Exception {
    super(engine, config);
  }

  private List<Site> getSites() throws Exception {
    return hardCache.get(sitesSupplier);
  }

  private Map<Integer, Double> getSiteDetails() {
    return hardCache.getUnchecked(siteDetailsSupplier);
  }

  @Override
  protected Iterator<String> getDocumentIds(boolean forTest, Random random) throws Exception {
    ResultSet resultSet = DB.getDocumentIds(
        getEngine().getEnvironment().getDatabase(), forTest, getSiteDetails().keySet());
    return new SiteBasedSamplingIterator(new ScalarResultSetIterator<String>(resultSet),
        random.nextLong(), getEngine().getEnvironment().getSampleProbability());
  }

  @Override
  public boolean isValid() throws Exception {
    return !getSites().isEmpty();
  }
}