/*
   Copyright 2009 jtpl.sourceforge.net

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
package net.sf.jtpl;

import net.sourceforge.jtpl.Jtpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The forward compatible template engine interface,
 * replacing {@link net.sourceforge.jtpl.Jtpl}.
 */
public class Template {

	private Jtpl t;
	
	public Template(Reader template) throws IOException {
		t = new Jtpl(template);
	}

	public Template(String templateSource) {
		try {
			t = new Jtpl(new StringReader(templateSource)); 
		} catch (IOException e) {
			throw new RuntimeException(e); // should be impossible with StringReader
		}
	}
	
    public void reset() {
        t.reset();
    }

	public Template(File templateFile) throws FileNotFoundException {
		//FileReader r = new FileReader(templateFile);
		try {
			t = new Jtpl(templateFile);
		} catch (IOException e) {
			try {
				//r.close();
			} catch (Exception nothingToDoAboutIt) {}
		}
	}
	
	// proxying methods from Jtpl1

	public Template assign(String varName, String varData) {
		if (varData == null) {
			throw new IllegalArgumentException("Null value assignment is not allowed. Assign empty string instead.");
		}
		t.assign(varName, varData);
		return this;
	}

	/**
	 * Render a block using the variables that have been assigned,
	 * and the contained blocks that have been rendered.
	 * The method name reflects the implementation, sort of,
	 * but a better name would be "render".
	 * @param blockName The block to render, dot syntax for sub block
	 * @return The template instance for chaining
	 * @throws IllegalArgumentException For unrecognized block
	 */
	public Template parse(String blockName) throws IllegalArgumentException {
		t.parse(blockName);
		return this;
	}	
	
	public String out() {
		return t.out();
	}
	
	// bean properties support
	
	public Template parse(String blockName, Object bean) {
		assignAll(bean);
		return parse(blockName);
	}
	
	public String out(Object bean) {
		assignAll(bean);
		return out();
	}
	
	protected void assignAll(Object bean) {
		Map p = getBeanProperties(bean);
		Iterator i = p.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next(); 
			assign(key.toUpperCase(), "" + p.get(key));
		}
	}
	
	/**
	 * @param bean The instance that has properties named according to JavaBean standard.
	 * @return Map<String, Object> that should be considered immutable
	 */
	protected Map getBeanProperties(Object bean) {
		HashMap values = new HashMap();
		if (bean == null) return values;
		Method[] m = bean.getClass().getMethods();
		
		Pattern p = Pattern.compile("get([A-Z]\\w+)");
		
		for (int i = 0; i < m.length; i++) {
			if (m[i].getName().equals("getClass")) continue;
			if (m[i].getParameterTypes().length > 0) continue;
			Matcher r = p.matcher(m[i].getName());
			if (r.matches()) {
				try {
					values.put(r.group(1).toLowerCase(), m[i].invoke(bean, new Object[0]));
				} catch (IllegalArgumentException e) {
					throw e;
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return values;
	}
	
}
