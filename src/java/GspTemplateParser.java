import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.text.Template;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: elattanzio
 * Date: 01/08/14
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class GspTemplateParser {

    public String text = "";

    public List<String> comments = new ArrayList<String>();
    public List<String> expressions = new ArrayList<String>();


    /**
     * Parse the text document looking for <% or <%= and then call out to the appropriate handler, otherwise copy the text directly
     * into the script while escaping quotes.
     *
     * @param reader a reader for the template text
     * @return the parsed text
     * @throws java.io.IOException if something goes wrong
     */
    public String parse(Reader reader) throws IOException {
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader);
        }
        StringWriter sw = new StringWriter();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '<') {
                reader.mark(1);
                c = reader.read();
                if (c != '%') {
                    sw.write('<');
                    reader.reset();
                } else {
                    reader.mark(2);
                    if (reader.read() == '-' && reader.read() == '-') {
                        commentSection2(reader, sw);
                    } else {
                        reader.reset();
                        reader.mark(1);
                        c = reader.read();
                        if (c == '=') {
                            groovyExpression(reader, sw);
                        } else {
                            reader.reset();
                            groovySection(reader, sw);
                        }
                    }
                }
                continue; // at least '<' is consumed ... read next chars.
            }

            if (c == '%') {
                reader.mark(1);
                c = reader.read();
                if (c != '{') {
                    sw.write('%');
                    reader.reset();
                } else {
                    reader.mark(2);
                    c = reader.read();
                    if (c == '-') {
                        c = reader.read();
                        if (c == '-') {
                            commentSection(reader, sw);
                        } else {
                            reader.reset();
                        }
                    } else {
                        reader.reset();
                    }
                }
                continue; // at least '<' is consumed ... read next chars.
            }

            if (c == '$') {
                reader.mark(1);
                c = reader.read();
                if (c != '{') {
                    sw.write('$');
                    reader.reset();
                } else {
                    reader.mark(1);

                    processGSstring(reader, sw);
                }
                continue; // at least '$' is consumed ... read next chars.
            }
            if (c == '\"') {
//                sw.write('\\');
            }
                /*
                 * Handle raw new line characters.
                 */
            if (c == '\n' || c == '\r') {
                if (c == '\r') { // on Windows, "\r\n" is a new line.
                    reader.mark(1);
                    c = reader.read();
                    if (c != '\n') {
                        reader.reset();
                    }
                }
                sw.write("\n");
                continue;
            }
            sw.write(c);
        }
        text = sw.toString();
        return text;
    }


    private void processGSstring(Reader reader, StringWriter out) throws IOException {
        StringWriter sw = new StringWriter();
        sw.write("${");
        int c;
        while ((c = reader.read()) != -1) {
            if (c != '\n' && c != '\r') {
                sw.write(c);
            }

            if (c == '}') {
                break;
            }
        }
        String str = sw.toString();
        expressions.add(str);
        out.write("<!-- ");
        out.write(str);
        out.write(" -->");
    }

    /**
     * Closes the currently open write and writes out the following text as a GString expression until it reaches an end %>.
     *
     * @param reader a reader for the template text
     * @param sw     a StringWriter to write expression content
     * @throws IOException if something goes wrong
     */
    private void groovyExpression(Reader reader, StringWriter out) throws IOException {
        StringWriter sw = new StringWriter();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '%') {
                c = reader.read();
                if (c != '>') {
                    sw.write('%');
                } else {
                    break;
                }
            }
            if (c != '\n' && c != '\r') {
                sw.write(c);
            }
        }
        String str = sw.toString();
        expressions.add(str);
        out.write("<!-- ");
        out.write(str);
        out.write(" -->");
    }

    /**
     * Closes the currently open write and writes the following text as normal Groovy script code until it reaches an end %>.
     *
     * @param reader a reader for the template text
     * @param sw     a StringWriter to write expression content
     * @throws IOException if something goes wrong
     */
    private void groovySection(Reader reader, StringWriter sw) throws IOException {
        groovyExpression(reader, sw);
    }

    /**
     * Comment style <%-- --%>
     */
    private void commentSection2(Reader reader, StringWriter sw) throws IOException {
        StringWriter buffer = new StringWriter();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '-') {
                reader.mark(3);
                if (reader.read() == '-' && reader.read() == '%' && reader.read() == '>') {
                    break;
                } else {
                    reader.reset();
                }
            }
                /* Don't eat EOL chars in sections - as they are valid instruction separators.
                 * See http://jira.codehaus.org/browse/GROOVY-980
                 */
            // if (c != '\n' && c != '\r') {
            buffer.write(c);
            //}
        }

        String str = buffer.toString();
        comments.add(str);
        sw.write("<!-- COMMENT UKE ");
        sw.write(str);
        sw.write(" COMMENT UKE -->");
    }

    /**
     * * Comment style %{-- --}%
     *
     * @param reader
     * @param sw
     * @throws IOException
     */
    private void commentSection(Reader reader, StringWriter sw) throws IOException {
        StringWriter buffer = new StringWriter();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '-') {
                reader.mark(3);
                if (reader.read() == '-' && reader.read() == '}' && reader.read() == '%') {
                    break;
                } else {
                    reader.reset();
                }
            }
                /* Don't eat EOL chars in sections - as they are valid instruction separators.
                 * See http://jira.codehaus.org/browse/GROOVY-980
                 */
            // if (c != '\n' && c != '\r') {
            buffer.write(c);
            //}
        }

        String str = buffer.toString();
        comments.add(str);
        sw.write("<!-- COMMENT UKE ");
        sw.write(str);
        sw.write(" COMMENT UKE -->");
    }

}

