package net.sf.jtpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Templates may be loaded any way you want using the different constructor,
 * but this helper supports the practice of keeping them in the same package
 * as the class that loads them.
 * <p>
 * When bundling templates inside jars the files should really be in a folder
 * matching the namespace of the component, so they don't collide with files
 * of the same name from other jars.
 */
public abstract class TemplateFactory {

	/**
	 * Loads template for a class assuming that it is placed in the corresponding folder
	 * in <code>src/main/resources/</code> (maven project) or in the same package in a jar.
	 * @param filename The filename of the template
	 * @param samePackage The class that loads the template, providing namespace and class loader
	 * @return The template
	 * @throws RuntimeException if loading fails, considered unrecoverable, wrapping IOException
	 */
	public static Template getTemplate(String filename, Class samePackage) {
		String parent = samePackage.getPackage().getName().replace('.', '/');
		String path = parent + '/' + filename;
		InputStream htmlin = samePackage.getClassLoader().getResourceAsStream(path);
		if (htmlin == null) {
			throw new RuntimeException("Failed to load template " + path);
		}
		Reader htmlr = new InputStreamReader(htmlin);
		// TODO should we buffer content here or leave it to Template to take adequate care of the reader?
		try {
			return new Template(htmlr);
		} catch (IOException e) {
			// Really not recoverable
			throw new RuntimeException("Unable to read classpath template " + path, e);
		}
	}
	
}
