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
package uk.ac.ed.inf.ace;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;
import uk.ac.ed.inf.ace.utils.Utilities;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Config {

  private static final String DEFAULT_PATH = "config.xml";
  private static final String CONFIG_XSD_RESOURCE_NAME = "config.xsd";

  private Config() {
  }

  public static Engine<?, ?> load(String configPath, String environmentName,
      String extensionSchemaPath, String extensionContextPath) throws Exception {
    configPath = Utilities.ifNull(configPath, DEFAULT_PATH);
    File file = new File(configPath);
    Preconditions.checkState(file.exists(), "Config file missing");
    Schema schema;

    try (InputStream inputStream = uk.ac.ed.inf.ace.config.v1.Engine.class.getResourceAsStream(
            CONFIG_XSD_RESOURCE_NAME)) {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      StreamSource[] sources;

      if (extensionSchemaPath != null) {
        sources = new StreamSource[]{new StreamSource(inputStream), new StreamSource(new File(
          extensionSchemaPath))};
      } else {
        sources = new StreamSource[]{new StreamSource(inputStream)};
      }

      schema = schemaFactory.newSchema(sources);
    }

    ValidatorHandler validatorHandler = schema.newValidatorHandler();
    JAXBContext jaxbContext = JAXBContext.newInstance(
        Utilities.ifNull(extensionContextPath, "") + ":uk.ac.ed.inf.ace.config.v1");
    UnmarshallerHandler unmarshallerHandler =
        jaxbContext.createUnmarshaller().getUnmarshallerHandler();
    validatorHandler.setContentHandler(unmarshallerHandler);
    TransformerFactory.newInstance().newTransformer().transform(new StreamSource(file),
        new SAXResult(validatorHandler));
    uk.ac.ed.inf.ace.config.v1.EngineBase engineBase =
        (uk.ac.ed.inf.ace.config.v1.EngineBase) unmarshallerHandler.getResult();
    @SuppressWarnings("unchecked")
    Class<? extends Engine<?, ?>> type =
        (Class<? extends Engine<?, ?>>) Class.forName(engineBase.getType());
    return Utilities.construct(type, new Class[]{engineBase.getClass(), String.class},
        new Object[]{engineBase, environmentName});
  }
}