/*
   Copyright 2015 David Tanzer (business@davidtanzer.net / @dtanzer)

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
package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;

/**
 * Converts an {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo} into a formatted string.
 *
 * To implement your own formatter, you only have to implement this interface. But there is also
 * {@link net.davidtanzer.jobjectformatter.formatter.AbstractObjectStringFormatter} that has some convenient functionality
 * for implementing formatters. So in most cases, you will want to extend AbstractObjectStringFormatter instead of implementing
 * this interface directly.
 *
 * @see net.davidtanzer.jobjectformatter.formatter.AbstractObjectStringFormatter
 */
public interface ObjectStringFormatter {
	/**
	 * Converts an {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo} into a formatted string.
	 *
	 * @param info The {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo} that contains all relevant information about the object to format.
	 * @return The formatted representation of the object, as String.
	 */
	String format(ObjectValuesInfo info);
}
