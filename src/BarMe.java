import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BarMe {
    static class Bar {
        public Bar(double latitude, double longitude, String placeName, double open, double closed) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeName = placeName;
            this.open = open;
            this.closed = closed;
        }

        public double latitude;
        public double longitude;
        public String placeName;
        public double open;
        public double closed;
        public int distance;
    }

    public static int calculateDistance(double lat1, double lon1, double lat2, double lon2) { // double
        var R = 6378.137; // Radius of earth in KM
        var dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        var dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c;
        return (int) (d * 1000); // meters

    }

    public static ArrayList<Bar> sortByDistance(ArrayList<Bar> barArrayList) {
        Bar temp;
        Bar[] array = barArrayList.toArray(new Bar[0]);
        for (int i = 0; i < array.length; i++) {
            for (int j = 1; j < (array.length - i); j++) {
                if (array[j - 1].distance > array[j].distance) {
                    // swap the elements!
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }
            }
        }
        return new ArrayList<>(List.of(array));
    }
    public static ArrayList<Bar> sortByTime(ArrayList<Bar> barArrayList) {
        Bar temp;
        Bar[] array = barArrayList.toArray(new Bar[0]);
        for (int i = 0; i < array.length; i++) {
            for (int j = 1; j < (array.length - i); j++) {
                if (array[j - 1].closed > array[j].closed) {
                    // swap the elements!
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }
            }
        }
        return new ArrayList<>(List.of(array));
    }

    public static ArrayList<Bar> filterByOpen(double time, ArrayList<Bar> barArrayList) {
        ArrayList<Bar> openArrayList = new ArrayList<>();

        for (Bar current : barArrayList) {
            if (current.open <= time && time <= current.closed) {
                openArrayList.add(current);
            } else if (current.closed < current.open) {
                if (time <= (current.closed + (time > current.closed ? 24 : 0))) {
                    openArrayList.add(current);
                }
            }
        }
        return openArrayList;
    }

    public static void findNearest(double latitude, double longitude, ArrayList<Bar> barArrayList) {
        for (Bar current : barArrayList) {
            current.distance = calculateDistance(latitude, longitude, current.latitude, current.longitude);
        }
    }

    public static void printArray(ArrayList<Bar> barArrayList) {
        for (int i = 0; i < barArrayList.size(); i++) {
            Bar current = barArrayList.get(i);
            System.out.printf("%d %s (%.2f-%.2f) - %dм %n", i + 1, current.placeName, current.open, current.closed, current.distance);
        }
    }

    public static void showMap(ArrayList<Bar> barArrayList, int myDistance) {
        List<Integer> distances = barArrayList.stream().map(x -> x.distance).collect(Collectors.toList());
        distances.add(myDistance);
        Collections.sort(distances);

        for (int i = 0; i < distances.size(); i++) {
            int current = distances.get(i);
            int lineSize = current / 50;
            for (int j = 0; j < lineSize; j++) {
                System.out.print("_");
            }
            String myPosition = current == myDistance ? "X" : String.valueOf(i + 1);
            System.out.print(myPosition);
        }
    }
    public static void showOptions(double latitude, double longitude, ArrayList<Bar> barArrayList) {

        System.out.println("\nИзберете опция: СПИСЪК ВСИЧКИ (1), СПИСЪК ОТВОРЕНИ (2), КАРТА (3)");
        Scanner scanner = new Scanner(System.in);
        byte option = Byte.parseByte(scanner.next().trim());

        findNearest(latitude, longitude, barArrayList);

        switch (option) {
            case 1 -> {
                barArrayList = sortByDistance(barArrayList);
                printArray(barArrayList);

            }
            case 2 -> {
                ArrayList<Bar> sortedList = sortByTime(barArrayList);
                ArrayList<Bar> filteredList = filterByOpen(09.30, sortedList);

                printArray(filteredList);
            }
            case 3 -> {
                System.out.println("Въведете локацията си (В Метри)");
                int myDistance = Integer.parseInt(scanner.next().trim());
                ArrayList<Bar> sortedList = sortByDistance(barArrayList);
                showMap(sortedList, myDistance);
                System.out.println();
                printArray(sortedList);
            }
            default -> System.out.println("Моля, въведете валидна опция!");
        }
        showOptions(latitude, longitude, barArrayList);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Въведете текущата си локация (дължина и ширина)");
        String line = scanner.nextLine();
        String[] split = line.split("\\s+");
        double longitude = Double.parseDouble(split[0].trim());
        double latitude = Double.parseDouble(split[1].trim());

        System.out.println(latitude + " " + longitude);

        // 26.5274221752 43.5189385

        ArrayList<Bar> barArrayList = new ArrayList<>();
        barArrayList.add(new Bar(43.52555309, 26.5222907, "SOHO", 09.00, 24.00));
        barArrayList.add(new Bar(43.5245368831, 26.5234081211, "Crazy Forest", 07.30, 01.00));
        barArrayList.add(new Bar(43.5189385, 26.5206696, "Милениум", 09.00, 23.30));
        barArrayList.add(new Bar(43.5312451, 26.5294561, "Лятно Кино", 08.00, 24.00));
        barArrayList.add(new Bar(43.527119, 26.5184806, "FAMOUS", 08.00, 23.30));
        barArrayList.add(new Bar(43.5252341738, 26.5274221756, "ENJOY", 08.00, 24.00));
        barArrayList.add(new Bar(43.53212, 26.5258, "Българе", 11.00, 23.45));
        barArrayList.add(new Bar(43.51745099, 26.5225625038, "Mozzarella", 08.30, 24.00));
        barArrayList.add(new Bar(43.5251203, 26.5231573, "Капитан Блъд", 07.00, 01.00));
        barArrayList.add(new Bar(43.529097324, 26.5242516897, "La Pastaria", 08.00, 23.00));
        barArrayList.add(new Bar(43.52645, 26.52943, "Сръбска скара", 09.00, 23.45));

        showOptions(latitude, longitude, barArrayList);

    }
}





