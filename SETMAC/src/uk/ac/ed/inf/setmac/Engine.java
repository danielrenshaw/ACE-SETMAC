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
package uk.ac.ed.inf.setmac;

import java.util.List;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;
import uk.ac.ed.inf.ace.utils.Utilities;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Engine
    extends uk.ac.ed.inf.ace.Engine<uk.ac.ed.inf.setmac.Engine, uk.ac.ed.inf.setmac.config.v1.Engine> {

  private final HardCache hardCache = new HardCache("EngineExt");
  private final SupplierEx<List<Site>> sitesSupplier =
      new SupplierEx<List<Site>>() {
        @Override
        public List<Site> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getSites().getSite(), Engine.this,
              Site.class);
        }
      };

  public Engine(uk.ac.ed.inf.setmac.config.v1.Engine config, String environmentName) {
    super(config, environmentName);
  }

  public Iterable<Site> getSites() throws Exception {
    return hardCache.get(sitesSupplier);
  }
}