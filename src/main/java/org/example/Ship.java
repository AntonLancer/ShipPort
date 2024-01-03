package org.example;
import java.util.concurrent.ThreadLocalRandom;

class Ship {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    static final int park_size = 5;
    static int max_places = 0;
    static final boolean[] park_places = new boolean[park_size];
    static final boolean[] bread_port = new boolean[1];
    static final boolean[] banana_port =new boolean[1];
    static final boolean[] clothes_port = new boolean[1];
    
    public static void main(String[] args) throws InterruptedException {
        banana_port[0] = false;
        bread_port[0] = false;
        clothes_port[0] = false;
        for (int i = 0; i < 10; i++) {

            int random = ThreadLocalRandom.current().nextInt(1, 3 + 1);
            int rand_gruz = ThreadLocalRandom.current().nextInt(1, 5 + 1);
            switch(random) {
                case 1: new Thread(new Car(i,"Banana", rand_gruz, false)).start(); break;
                case 2: new Thread(new Car(i,"Bread", rand_gruz, false)).start(); break;
                case 3: new Thread(new Car(i,"Clothes", rand_gruz, false)).start(); break;
            }
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 600 + 1));
        }
    }
    static class Car implements Runnable {
        private int carNumber;
        private String type_gruz;
        private int gruz;

        private boolean finish;

        public void set_finish(boolean fin){
            this.finish = fin;
        }
        Car(int carNumber, String gruz, int gruzkol, boolean fin) {
            this.carNumber = carNumber;
            this.type_gruz = gruz;
            this.gruz=gruzkol;
            this.finish = fin;
        }
        @Override
        public void run() {
            System.out.printf("Корабль №%d создан.\n", carNumber);
            try {
                int parkingNumber = -1;
                synchronized (park_places) {
                    if(max_places == park_size) {
                        park_places.wait();
                    }
                    for (int i = 0; i < park_size; i++) {
                        if (!park_places[i]) {

                            park_places[i] = true;
                            parkingNumber = i;
                            max_places = max_places + 1;
                            System.out.printf(ANSI_YELLOW + "Корабль №%d занял место %d. Тип груза %s. Количество груза %d\n" + ANSI_RESET, carNumber, parkingNumber + 1, type_gruz, gruz);
                            break;
                        }
                    }
                }
                do {
                    if (type_gruz.equals("Banana")) {
                        synchronized (banana_port) {
                            banana_port[0] = true;
                            synchronized (park_places) {
                                max_places--;
                                park_places[parkingNumber] = false;
                                park_places.notify();
                            }
                            for (int j = 0; j < gruz; j++) {
                                System.out.printf(ANSI_RED + "Бананы разгружает корабль номер %d\n" + ANSI_RESET, carNumber);
                                Thread.sleep(3000);

                            }
                            System.out.printf(ANSI_GREEN + "Бананы разгружены, корабль %d уплывает\n" + ANSI_RESET, carNumber);
                            banana_port[0] = false;
                            set_finish(true);

                        }
                    }
                    if (type_gruz.equals("Bread")) {
                        synchronized (bread_port) {
                            bread_port[0] = true;
                            synchronized (park_places) {
                                max_places--;
                                park_places[parkingNumber] = false;
                                park_places.notify();
                            }
                            for (int j = 0; j < gruz; j++) {
                                System.out.printf(ANSI_RED + "Хлеб разгружает корабль номер %d\n" + ANSI_RESET, carNumber);
                                Thread.sleep(3000);
                            }
                            System.out.printf(ANSI_GREEN + "Хлеб разгружен, корабль %d уплывает\n" + ANSI_RESET, carNumber);
                            bread_port[0] = false;
                            set_finish(true);
                        }
                    }
                    if (type_gruz.equals("Clothes")) {
                        synchronized (clothes_port) {
                            clothes_port[0] = true;
                            synchronized (park_places) {
                                max_places--;
                                park_places[parkingNumber] = false;
                                park_places.notify();
                            }
                            for (int j = 0; j < gruz; j++) {
                                System.out.printf(ANSI_RED + "Одежду разгружает корабль номер %d\n" + ANSI_RESET, carNumber);
                                Thread.sleep(3000);
                            }
                            System.out.printf(ANSI_GREEN + "Одежда разгружена, корабль %d уплывает\n" + ANSI_RESET, carNumber);
                            clothes_port[0] = false;
                            set_finish(true);
                        }
                    }
                } while (!finish);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}