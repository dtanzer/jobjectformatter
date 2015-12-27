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
package net.davidtanzer.jobjectformatter.annotations;

/**
 * With FormattedFieldType, you can configure when a field is included in the formatted output using the
 * {@link net.davidtanzer.jobjectformatter.annotations.FormattedField} annotation.
 *
 * @see net.davidtanzer.jobjectformatter.annotations.FormattedField
 */
public enum FormattedFieldType {
	/**
	 * Include the field in the formatted output.
	 */
	DEFAULT,
	/**
	 * Include the field in the formatted output, but only when the output is set to "verbose".
	 */
	VERBOSE,
	/**
	 * Do not include the field in the formatted output.
	 */
	NEVER
}
