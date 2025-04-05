import java.util.*;

public class ProcessSchedulerSimulator {
    // Class to represent a process
    static class Process {
        int id, arrivalTime, burstTime, priority;
        int startTime = -1, completionTime, turnaroundTime, waitingTime;

        Process(int id, int arrivalTime, int burstTime, int priority) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }

    // First Come First Serve Scheduling
    public static void fcfs(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        for (Process p : processes) {
            p.startTime = Math.max(currentTime, p.arrivalTime);
            p.completionTime = p.startTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime = p.completionTime;
        }
        drawGanttChart(processes, "FCFS");
        printResults(processes);
    }

    // Shortest Job First Scheduling
    public static void sjf(List<Process> processes) {
        List<Process> completed = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        int time = 0;
        while (completed.size() < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= time && !completed.contains(p) && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }
            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }
            readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
            Process p = readyQueue.remove(0);
            p.startTime = Math.max(time, p.arrivalTime);
            p.completionTime = p.startTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            time = p.completionTime;
            completed.add(p);
        }
        drawGanttChart(completed, "SJF");
        printResults(completed);
    }

    // Priority Scheduling
    public static void priorityScheduling(List<Process> processes) {
        List<Process> completed = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        int time = 0;
        while (completed.size() < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= time && !completed.contains(p) && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }
            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }
            readyQueue.sort(Comparator.comparingInt(p -> p.priority));
            Process p = readyQueue.remove(0);
            p.startTime = Math.max(time, p.arrivalTime);
            p.completionTime = p.startTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            time = p.completionTime;
            completed.add(p);
        }
        drawGanttChart(completed, "Priority");
        printResults(completed);
    }

    // Round Robin Scheduling
    public static void roundRobin(List<Process> processes, int quantum) {
        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        Map<Process, Integer> remainingTime = new HashMap<>();
        for (Process p : processes) remainingTime.put(p, p.burstTime);

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int index = 0;
        List<String> gantt = new ArrayList<>();
        List<Integer> timeStamps = new ArrayList<>();
        timeStamps.add(0);
        Set<Process> seen = new HashSet<>();

        while (!queue.isEmpty() || index < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= time) {
                queue.offer(processes.get(index));
                seen.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                time++;
                timeStamps.add(time);
                continue;
            }

            Process p = queue.poll();
            if (p.startTime == -1) {
                p.startTime = time;
            }

            int executeTime = Math.min(quantum, remainingTime.get(p));
            for (int t = 0; t < executeTime; t++) {
                gantt.add("P" + p.id);
                time++;
                timeStamps.add(time);
            }
            remainingTime.put(p, remainingTime.get(p) - executeTime);

            while (index < processes.size() && processes.get(index).arrivalTime <= time) {
                if (!seen.contains(processes.get(index))) {
                    queue.offer(processes.get(index));
                    seen.add(processes.get(index));
                }
                index++;
            }

            if (remainingTime.get(p) > 0) {
                queue.offer(p);
            } else {
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }
        }
        drawGanttChartFromList(gantt, timeStamps, "Round Robin");
        printResults(processes);
    }

    // Gantt Chart using process list (for FCFS, SJF, Priority)
    public static void drawGanttChart(List<Process> processes, String title) {
        System.out.println("\n--- Gantt Chart: " + title + " ---");
        for (Process p : processes) {
            System.out.print("|  P" + p.id + "  ");
        }
        System.out.println("|");
        for (Process p : processes) {
            System.out.print(p.startTime + "     ");
        }
        System.out.println(processes.get(processes.size() - 1).completionTime);
    }

    // Gantt Chart using time slices (for Round Robin)
    public static void drawGanttChartFromList(List<String> sequence, List<Integer> timeStamps, String title) {
        System.out.println("\n--- Gantt Chart: " + title + " ---");
        for (String s : sequence) {
            System.out.print("| " + s + " ");
        }
        System.out.println("|");
        for (int t : timeStamps) {
            System.out.print(t + "   ");
        }
        System.out.println();
    }

    // Print all process statistics
    public static void printResults(List<Process> processes) {
        System.out.println("ID\tArrival\tBurst\tStart\tComplete\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                    p.id, p.arrivalTime, p.burstTime, p.startTime,
                    p.completionTime, p.turnaroundTime, p.waitingTime);
        }
    }

    // Main method to test all scheduling algorithms
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 0, 4, 2));
        processes.add(new Process(2, 1, 3, 1));
        processes.add(new Process(3, 2, 1, 3));
        processes.add(new Process(4, 3, 2, 4));

        System.out.println("\n--- FCFS Scheduling ---");
        fcfs(cloneProcesses(processes));

        System.out.println("\n--- SJF Scheduling ---");
        sjf(cloneProcesses(processes));

        System.out.println("\n--- Priority Scheduling ---");
        priorityScheduling(cloneProcesses(processes));

        System.out.println("\n--- Round Robin Scheduling (Quantum=2) ---");
        roundRobin(cloneProcesses(processes), 2);
    }

    
    private static List<Process> cloneProcesses(List<Process> original) {
        List<Process> clone = new ArrayList<>();
        for (Process p : original) {
            clone.add(new Process(p.id, p.arrivalTime, p.burstTime, p.priority));
        }
        return clone;
    }
}
