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

import java.io.File;
import uk.ac.ed.inf.ace.NamedBase;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;

/**
 * This should be an extension class, not tied into the core XSD. However, changing it would require
 * implementing a great deal of custom validation logic, since the extension XSD is being ignored
 * right now.
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Site extends NamedBase<Engine, uk.ac.ed.inf.setmac.config.v1.Site> {

  private final HardCache hardCache = new HardCache("Site");
  private final SupplierEx<File> xmlDirectorySupplier = new SupplierEx<File>() {
    @Override
    public File get() throws Exception {
      return new File(getConfig().getXmlPath());
    }
  };

  public Site(Engine engine, uk.ac.ed.inf.setmac.config.v1.Site config) {
    super(engine, config);
  }

  public File getXmlFile(String pathname) throws Exception {
    return new File(hardCache.get(xmlDirectorySupplier), pathname);
  }

  public double getSampleProbability() {
    return getConfig().getSampleProbability().doubleValue();
  }
}