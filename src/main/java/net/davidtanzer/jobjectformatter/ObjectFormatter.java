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
package net.davidtanzer.jobjectformatter;

import java.util.concurrent.atomic.AtomicReference;

public class ObjectFormatter {
	private static AtomicReference<FormattedStringGenerator> generator = new AtomicReference<>(new FormattedStringGenerator());

	public static void configureGenerator(final FormattedStringGenerator generator) {
		ObjectFormatter.generator.set(generator);
	}

	public static String format(final Object object) {
		return generator.get().format(object);
	}
}
