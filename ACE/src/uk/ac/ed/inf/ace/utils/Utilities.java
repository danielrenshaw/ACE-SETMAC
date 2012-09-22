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
package uk.ac.ed.inf.ace.utils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import java.lang.reflect.Constructor;
import java.util.List;
import javax.xml.bind.JAXBElement;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.Named;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Utilities {

  private Utilities() {
  }

  public static <T> T ifNull(T value, T def) {
    return value == null ? def : value;
  }

  public static <T> T construct(Class<T> type, Class[] parameterTypes, Object[] parameterValues)
      throws Exception {
    Constructor<T> constructor = type.getConstructor(parameterTypes);
    return constructor.newInstance(parameterValues);
  }

  public static <C> Iterable<C> extractConfigs(Iterable<JAXBElement<? extends C>> configs) {
    if (configs == null) {
      return null;
    }

    return Iterables.transform(configs, new Function<JAXBElement<? extends C>, C>() {
      @Override
      public C apply(JAXBElement<? extends C> element) {
        return element.getValue();
      }
    });
  }

  public static <E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.Enableable, T extends Named> List<T> getConfiguredItems(
      List<JAXBElement<? extends C>> configs, E engine, Class<T> currentType) throws Exception {
    FunctionEx<C, T> constructor = constructDefaultConstructor(engine, currentType, null);
    return getConfiguredItems(extractConfigs(configs), constructor, null);
  }

  public static <E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.Enableable, T extends Named> List<T> getConfiguredItems(
      List<JAXBElement<? extends C>> configs, E engine, Class<T> currentType, Iterable<T> globals)
      throws Exception {
    FunctionEx<C, T> constructor = constructDefaultConstructor(engine, currentType, globals);
    return getConfiguredItems(extractConfigs(configs), constructor, globals);
  }

  public static <E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.Enableable, T extends Named> List<T> getConfiguredItems(
      Iterable<C> configs, E engine, Class<T> currentType) throws Exception {
    FunctionEx<C, T> constructor = constructDefaultConstructor(engine, currentType, null);
    return getConfiguredItems(configs, constructor, null);
  }

  public static <E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.Enableable, T extends Named> List<T> getConfiguredItems(
      Iterable<C> configs, E engine, Class<T> currentType, Iterable<T> globals) throws Exception {
    FunctionEx<C, T> constructor = constructDefaultConstructor(engine, currentType, globals);
    return getConfiguredItems(configs, constructor, globals);
  }

  public static <T extends Named> T getReferencedConfiguredItem(String name, Iterable<T> globals)
      throws Exception {
    for (T item : globals) {
      if (item.getName().equalsIgnoreCase(name)) {
        return item.isValid() ? item : null;
      }
    }

    return null;
  }

  private static <E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.Enableable, T extends Named> FunctionEx<C, T> constructDefaultConstructor(
      final E engine, final Class<T> currentType, final Iterable<T> globals) {
    return new FunctionEx<C, T>() {
      @Override
      public T apply(C config) throws Exception {
        if (config instanceof uk.ac.ed.inf.ace.config.v1.Reference) {
          return getReferencedConfiguredItem(config.getName(), globals);
        } else {
          Class<T> type;
          Class<?> engineType = engine.getClass();
          T item = null;

          if (config instanceof uk.ac.ed.inf.ace.config.v1.Typeable) {
            @SuppressWarnings("unchecked")
            Class<T> typedType =
                (Class<T>) Class.forName(((uk.ac.ed.inf.ace.config.v1.Typeable) config).getType());
            type = typedType;
          } else {
            type = currentType;
          }

          while (engineType != null) {
            try {
              item = construct(type, new Class[]{engineType, config.getClass()},
                  new Object[]{engine, config});
              break;
            } catch (NoSuchMethodException exception) {
              engineType = engineType.getSuperclass();
            }
          }

          if (item == null) {
            throw new Exception("Failed to construct an instance of " + type + ".");
          }

          return item.isValid() ? item : null;
        }
      }
    };
  }

  private static <C extends uk.ac.ed.inf.ace.config.v1.Enableable, T> List<T> getConfiguredItems(
      Iterable<C> configs, FunctionEx<C, T> constructor, Iterable<T> globals) throws Exception {
    if (configs == null) {
      return ImmutableList.copyOf(globals);
    } else {
      Builder<T> builder = ImmutableList.builder();

      for (C config : configs) {
        if (config.isEnabled()) {
          T item = constructor.apply(config);

          if (item != null) {
            builder.add(item);
          }
        }
      }

      return builder.build();
    }
  }
  public static final Function<String, Object> PARSE_INTEGER = new Function<String, Object>() {
    @Override
    public Object apply(String input) {
      return Integer.parseInt(input);
    }
  };
}