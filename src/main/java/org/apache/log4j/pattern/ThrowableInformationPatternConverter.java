/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.pattern;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Enhancement of the 'short' mode to also output the toString() of throwable's causes
 */
/**
 * Outputs the ThrowableInformation portion of the LoggingiEvent as a full stacktrace
 * unless this converter's option is 'short', where it just outputs the first line of the trace.
 *
 * @author Paul Smith
 *
 */
public class ThrowableInformationPatternConverter
  extends LoggingEventPatternConverter {
  /**
   * If "short", only first line of throwable report will be formatted.
   */
  private final String option;

  /**
   * Private constructor.
   * @param options options, may be null.
   */
  private ThrowableInformationPatternConverter(
    final String[] options) {
    super("Throwable", "throwable");

    if ((options != null) && (options.length > 0)) {
      option = options[0];
    } else {
      option = null;
    }
  }

  /**
   * Gets an instance of the class.
    * @param options pattern options, may be null.  If first element is "short",
   * only the first line of the throwable will be formatted.
   * @return instance of class.
   */
  public static ThrowableInformationPatternConverter newInstance(
    final String[] options) {
    return new ThrowableInformationPatternConverter(options);
  }

  /**
   * {@inheritDoc}
   */
  public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
    ThrowableInformation information = event.getThrowableInformation();

    if (information != null) {
      String[] stringRep;

      int length = 0;

      if (option == null) {
        stringRep = information.getThrowableStrRep();
        length = stringRep.length;
      } else if (option.equals("full")) {
        stringRep = information.getThrowableStrRep();
        length = stringRep.length;
      } else if (option.equals("short")) {
        StringBuilder msg = new StringBuilder();
        List<Throwable> throwables = getThrowableList(information.getThrowable());
        for(Throwable throwable : throwables) {
          msg.append(throwable.toString() + " ");
        }
        stringRep = new String[]{msg.toString()};
        length = 1;
      } else {
        stringRep = information.getThrowableStrRep();
        length = stringRep.length;
      }

      for (int i = 0; i < length; i++) {
        String string = stringRep[i];
        toAppendTo.append(string).append("\n");
      }
    }
  }

  /**
   * This converter obviously handles throwables.
   * @return true.
   */
  public boolean handlesThrowable() {
    return true;
  }
  
  /**
   * Builds the list of the given <tt>throwable</tt> and all its causes.
   * 
   * <p>Only relies on {@link Throwable#getCause()}.</p>
   * 
   * @param throwable
   * @return non <tt>null<tt> list of non <tt>null</tt> throwables
   * @see Throwable#getCause()
   */
  public List<Throwable> getThrowableList(Throwable throwable) {
    List<Throwable> throwables = new ArrayList<Throwable>();
    while (throwable != null && throwables.contains(throwable) == false) {
      throwables.add(throwable);
      throwable = throwable.getCause();
    }
    return throwables;
  }
}
