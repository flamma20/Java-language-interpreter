import java.io.*;
import java.util.*;
//Sail 2023 language interpreter by Ahmed Saeed
public class Sail2023 {
public static void main(String[] args) {
Scanner scan = new Scanner(System.in);
String menuOption;

// Main menu loop

    while (true) {
        System.out.println("Menu Options:");
        System.out.println("  r - Execute interpreter");
        System.out.println("  e - Exit");
        System.out.print("Enter your choice: ");
        menuOption = scan.nextLine().toLowerCase();

        if (menuOption.equals("e")) {
            System.out.println("Exiting the program.");
            break;
        } else if (menuOption.equals("r")) {
            System.out.print("Enter the file name: ");
            String fName = scan.nextLine();
            String dirPath = "TestFiles/";
            String inputFPath = dirPath + fName + ".txt";
            showFileName(inputFPath);
            processInputFile(inputFPath);
        } else {
            System.out.println("Invalid option. Please try again.");
        }
} 
}
//Displays the name of the file being processed.
public static void showFileName(String fPath) {
    File f = new File(fPath);
    System.out.println("Reading code from: " + f.getName());
}
//error handling for undefined variable 
public static void checkVariable(String varName, Map<String, Integer> vars) {
    if (!vars.containsKey(varName)) {
        throw new IllegalArgumentException("Undefined variable: " + varName);
    }
}
// Processes the input file and executes the custom language instructions
public static void processInputFile(String fPath) {
    boolean inIfBlock = false;
    boolean runIfBlock = false;
    
    Map<String, Integer> vars = new HashMap<>();
    List<String> codeLines = new ArrayList<>(); 
    int totalLinesCount = 0;
    int nonCommentLinesCount = 0;

    try (BufferedReader buffRead = new BufferedReader(new FileReader(fPath))) {
        String ln;
        while ((ln = buffRead.readLine()) != null) {
            totalLinesCount++;
            if (ln.trim().isEmpty() || ln.startsWith("/")) {
                continue;
            }
            codeLines.add(ln);
            nonCommentLinesCount++;
        }
    } catch (IOException e) {
        System.err.println("Error reading input file: " + e.getMessage());
        return;
    }

    System.out.println("Total lines read: " + totalLinesCount);
    System.out.println("Non-comment lines read: " + nonCommentLinesCount); 
    System.out.println("-----------------------------------------------------------"); 

    for (int i = 0; i < codeLines.size(); i++) {
        String[] parts = codeLines.get(i).split("\\s+", 2); 

        switch (parts[0].toLowerCase()) {
            case "set":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                String[] setParts = parts[1].split("\\s+");
                if (isNumeric(setParts[2])) {
                    vars.put(setParts[0], Integer.parseInt(setParts[2]));
                } else {
                    vars.put(setParts[0], vars.get(setParts[2]));
                }
                break;

            case "add":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                String[] addParts = parts[1].split("\\s+");
                checkVariable(addParts[2], vars);

                int addVal = vars.get(addParts[2]);
                addVal += isNumeric(addParts[0]) ? Integer.parseInt(addParts[0]) : vars.get(addParts[0]);
                vars.put(addParts[2], addVal);
                break;

            case "subtract":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                String[] subParts = parts[1].split("\\s+");
                checkVariable(subParts[2], vars);

                int subVal = vars.get(subParts[2]);
                subVal -= isNumeric(subParts[0]) ? Integer.parseInt(subParts[0]) : vars.get(subParts[0]);
                vars.put(subParts[2], subVal);
                break;

            case "multiply":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                String[] mulParts = parts[1].split("\\s+");
                checkVariable(mulParts[2], vars);

                int mulVal = vars.get(mulParts[0]);
                mulVal *= isNumeric(mulParts[2]) ? Integer.parseInt(mulParts[2]) : vars.get(mulParts[2]);
                vars.put(mulParts[0], mulVal);
                break; 
                            case "divide":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                String[] divParts = parts[1].split("\\s+");
                checkVariable(divParts[2], vars);

                int divVal = vars.get(divParts[0]);
                divVal /= isNumeric(divParts[2]) ? Integer.parseInt(divParts[2]) : vars.get(divParts[2]);
                vars.put(divParts[0], divVal);
                break;

            case "if":
                inIfBlock = true;
                String[] ifParts = parts[1].split("\\s+");
                int firstOper = isNumeric(ifParts[0]) ? Integer.parseInt(ifParts[0]) : vars.get(ifParts[0]);
                int secondOper = isNumeric(ifParts[2]) ? Integer.parseInt(ifParts[2]) : vars.get(ifParts[2]);
                switch (ifParts[1]) {
                    case "<":
                        runIfBlock = firstOper < secondOper;
                        break;
                    case ">":
                        runIfBlock = firstOper > secondOper;
                        break;
                    case "==":
                        runIfBlock = firstOper == secondOper;
                        break;
                    default:
                        System.err.println("Invalid comparison operator: " + ifParts[1]);
                        break;
                }
                break;
            

            case "endif":
                inIfBlock = false;
                runIfBlock = false;
                break;
            
            case "print":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                printOrPrintLn(parts[1], vars, false);
                break;

            case "println":
                if (inIfBlock && !runIfBlock) {
                    break;
                }
                printOrPrintLn(parts[1], vars, true);
                break;

            case "end":
                return;

            default:
                System.err.println("Invalid command: " + parts[0]);
                break;
        }
    }
}

public static void printOrPrintLn(String inputData, Map<String, Integer> vars, boolean newline) {
    if (inputData.equals("cls")) {
        System.out.print("\f");
        return;
    } else if (isNumeric(inputData)) {
        inputData = String.valueOf(Integer.parseInt(inputData));
    } else if (inputData.startsWith("'")) {
        inputData = inputData.substring(1, inputData.length() - 1);
    } else {
        checkVariable(inputData, vars);
        inputData = vars.get(inputData).toString();
    }
    if (newline) {
        System.out.println(inputData);
    } else {
        System.out.print(inputData);
    }
}
//Determines whether the provided string is a valid integer
public static boolean isNumeric(String s) {
    try {
        Integer.parseInt(s);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}
}
