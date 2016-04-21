package org.shvets.antlet.starter;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

/**
 * This class understand how tosupport interactive mode
 * from the command line.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public abstract class InteractiveStarter extends GenericStarter {

  /**
   * "Start" method.
   *
   * @param args   command line arguments
   * @param loader the class loader
   */
  public final void start(final String[] args, final ClassLoader loader)
         throws Throwable {
    if (!isInteractive(args)) {
      process(args, loader);
    }
    else {
      final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

      while (true) {
        System.out.print("Enter your target: ");

        String commandLine = keyboard.readLine();

        if(commandLine == null) {
          break;
        }

        commandLine = commandLine.trim();

        if(commandLine.equalsIgnoreCase("exit") ||
            commandLine.equalsIgnoreCase("q")) {
          break;
        }

        final StringTokenizer st = new StringTokenizer(commandLine);

        final String[] newArgs = new String[2 + st.countTokens()];

        newArgs[0] = args[0];
        newArgs[1] = args[1];

        for (int i = 2; st.hasMoreTokens(); i++) {
          newArgs[i] = st.nextToken();

          try {
            process(newArgs, loader);
          }
          catch (Throwable t) {
            // supress all exceptions to not to break the iteration
            t.printStackTrace();
          }
        }
      }
    }

    System.exit(0); // Due to bug in JFileChhoser
  }

  /**
   * Checks if the current mode is interactive.
   *
   * @param args command line arguments
   * @return true if the current launch is interactive;
   *         false otherwise
   */
  private boolean isInteractive(final String[] args) {
    for (int i = 0; i < args.length; ++i) {
      final String arg = args[i];

      if (arg.equalsIgnoreCase("-i")) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if the parameter of command line should be treated as special.
   * It requires for antlets to separate targets from regular parameters.
   *
   * @param arg the argument
   * @return true if the parameter is special; false otherwise
   */
  protected final boolean isSpecialArgument(final String arg) {
    return arg.startsWith("[") && arg.endsWith("]");
  }

  /**
   * "process" method.
   *
   * @param args   command line arguments
   * @param loader the class loader
   */
  protected void process(final String[] args, final ClassLoader loader)
            throws Throwable {
    super.start(args, loader);
  }

}
