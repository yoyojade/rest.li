/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.data.schema;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.linkedin.data.schema.DataSchemaConstants.ENUM_SYMBOL_PATTERN;

/**
 * {@link DataSchema} for enum.
 *
 * @author slim
 */
public final class EnumDataSchema extends NamedDataSchema
{
  public EnumDataSchema(Name name)
  {
    super(Type.ENUM, name);
  }

  /**
   * Set the symbols of the enum.
   *
   * @param symbols of the enum in the order they are defined.
   * @param errorMessageBuilder to append error message to.
   * @return false if one or more symbols are not valid or each
   *         symbol is not unique.
   */
  public boolean setSymbols(List<String> symbols, StringBuilder errorMessageBuilder)
  {
    boolean ok = true;
    if (symbols != null)
    {
      Map<String, Integer> map = new HashMap<String, Integer>();
      int index = 0;
      for (String symbol : symbols)
      {
        if (isValidEnumSymbol(symbol) == false)
        {
          errorMessageBuilder.append("\"").append(symbol).append("\" is an invalid enum symbol.\n");
          ok = false;
          setHasError();
        }
        if (map.containsKey(symbol))
        {
          errorMessageBuilder.append("\"").append(symbol).append(" \" defined more than once in enum symbols.\n");
          ok = false;
        }
        else
        {
          map.put(symbol, index);
        }
        index++;
      }
      _symbols = Collections.unmodifiableList(symbols);
      _symbolToIndexMap = Collections.unmodifiableMap(map);
    }
    if (ok == false)
    {
      setHasError();
    }
    return ok;
  }

  /**
   * Symbols in the order declared.
   *
   * @return symbols in the the order declared.
   */
  public List<String> getSymbols()
  {
    return _symbols;
  }

  /**
   * Returns the index of a symbol.
   *
   * @param symbol to obtain index for.
   * @return positive integer which is the index of the symbol if found else return -1.
   */
  public int index(String symbol)
  {
    Integer res = _symbolToIndexMap.get(symbol);
    return (res == null ? -1 : res);
  }

  /**
   * Returns whether the symbol is a member of the enum.
   *
   * @param symbol to check.
   * @return true if symbol is a member of the enum.
   */
  public boolean contains(String symbol)
  {
    return _symbolToIndexMap.containsKey(symbol);
  }

  @Override
  public boolean equals(Object object)
  {
    if (this == object)
    {
      return true;
    }
    if (object != null && object.getClass() == EnumDataSchema.class)
    {
      EnumDataSchema other = (EnumDataSchema) object;
      return super.equals(other) && _symbols.equals(other._symbols);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return super.hashCode() ^ _symbols.hashCode();
  }

  static public boolean isValidEnumSymbol(String symbol)
  {
    return ENUM_SYMBOL_PATTERN.matcher(symbol).matches();
  }

  private List<String> _symbols = _emptySymbols;
  private Map<String, Integer> _symbolToIndexMap = _emptySymbolToIndexMap;

  static private final List<String> _emptySymbols = Collections.emptyList();
  static private final Map<String, Integer> _emptySymbolToIndexMap = Collections.emptyMap();
}
