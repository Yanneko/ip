import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * Duke's Parser. Deals with making sense of the user command.
 */
public class Parser {
    private final TaskList taskList;
    private final Storage storage;

    public Parser(TaskList taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }

    /**
     * Takes in the user's command and decides the appropriate action to take, then returns the output string for
     * the UI object to display. Uses references to taskList, storage, scanner and ui in order to edit the task list,
     * store the task list when done, scan for arguments to commands and end the ui when needed.
     * @param input The user input for the parser to parse.
     * @return Output String for the associated UI object to display.
     */
    public String parse(String input) {
        assert input != null;
        StringBuilder outputString = new StringBuilder();
        String[] cmdWithArgs = input.split(" ", 2);
        switch (cmdWithArgs[0]) {
        case "list":
            outputString.append("Acknowledged. Listing tasks.\n");
            for (int taskNum = 0; taskNum < taskList.size(); taskNum++) {
                outputString.append(taskNum + 1).append(".").append(taskList.get(taskNum)).append("\n");
            }
            break;

        case "todo":
            String args = cmdWithArgs[1];
            if (args.strip().equals("")) {
                outputString.append("Warning: ToDo entry format incorrect.\n")
                        .append("Try 'todo <task name>'");
                break;
            }
            ToDo nextToDo = new ToDo(args.strip());
            taskList.add(nextToDo);
            outputString.append("Acknowledged. Adding task:\n").append(nextToDo).append("\n")
                    .append("Notice. You now have [").append(taskList.size()).append("] task(s).\n");
            break;

        case "deadline":
            String[] deadLineArgs = cmdWithArgs[1].split(" /by ");

            try {
                Deadline nextDeadLine = new Deadline(deadLineArgs[0].strip(), deadLineArgs[1]);
                taskList.add(nextDeadLine);
                outputString.append("Acknowledged. Adding deadline:\n").append(nextDeadLine).append("\n")
                        .append("Notice. You now have [").append(taskList.size()).append("] task(s).\n");
            } catch (IndexOutOfBoundsException | DateTimeParseException e) {
                outputString.append("Warning: Deadline entry format incorrect.\n")
                        .append("Try 'deadline <deadline title> /by <deadline date and time>'");
            }
            break;

        case "event":
            String[] eventArgs = cmdWithArgs[1].split(" /at ");

            try {
                Event nextEvent = new Event(eventArgs[0].strip(), eventArgs[1]);
                taskList.add(nextEvent);
                outputString.append("Acknowledged. Adding event:\n").append(nextEvent).append("\n")
                        .append("Notice. You now have [").append(taskList.size()).append("] task(s).\n");
            } catch (IndexOutOfBoundsException e) {
                outputString.append("Warning: Event entry format incorrect.\n")
                        .append("Try 'event <event title> /at <event date and time>'");
            }
            break;

        case "done":
            try {
                int doneTarget = Integer.parseInt(cmdWithArgs[1]);
                outputString.append("Understood. The following task has been completed:\n");
                Task targetTask = taskList.taskList.get(doneTarget - 1);
                targetTask.markAsDone();
                outputString.append(targetTask).append("\n");

            } catch (Exception e) {
                outputString.append("Warning: done command format incorrect.\n")
                        .append("Try 'done <index>'");
            }
            break;

        case "delete":
            try {
                int removeTarget = Integer.parseInt(cmdWithArgs[1]);
                outputString.append("Notice. The following task has been removed:\n");
                Task targetTask = taskList.get(removeTarget - 1);
                outputString.append(targetTask).append("\n");
                taskList.remove(removeTarget - 1);
            } catch (Exception e) {
                outputString.append("Warning: delete command format incorrect.\n")
                        .append("Try 'delete <index>'");
            }
            break;

        case "find":
            String searchString = cmdWithArgs[1].strip();
            outputString.append("Understood. The following tasks were found: \n");
            int j = 1;
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                if (task.taskName.contains(searchString)) {
                    outputString.append(j).append(".").append(task.toString()).append("\n");
                    j++;
                }
            }
            break;

        case "bye":
            try {
                storage.writeToStorage(taskList);
            } catch (IOException e) {
                outputString.append("IOException thrown, task list not saved.\n");
            }
            outputString.append("Goodbye.\n");
            break;

        case "help":
            outputString.append("list: lists tasks | ")
                    .append("todo: adds new todo task | ")
                    .append("deadline: adds new deadline | ")
                    .append("event: adds new event | ")
                    .append("done: marks task as done | ")
                    .append("delete: deletes task | ")
                    .append("find: finds tasks | ")
                    .append("bye: tells Sage to go away\n");
            break;

        default:
            outputString.append("Notice. Unfamiliar command detected. Enter 'help' to view commands.\n");
            break;
        }
        return outputString.toString();
    }
}
