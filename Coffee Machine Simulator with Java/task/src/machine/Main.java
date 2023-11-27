package machine;

import java.util.Scanner;

import static machine.CoffeeConstants.*;

/* Een CoffeeMachine object heeft als veld een Inventory object. Het Inventory object bestaat uit Resource objects.
Afzonderlijke drankjes zijn Drink objects, die hebben geen Resource velden om hun bestandsdelen aan te geven, maar
'normale' ints. Als uitdaging stelde ik om de Inventory mutable qua opzet te maken en de Drink objecten final.
In eerste instantie wilde ik voor de ingredienten in Drink ook Resource classes gebruiken, maar in dat geval was het
nog steeds mogelijk om na instantiering van de Drink objects de Resource velden aan te passen met de interface van
Resource.
*/

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory startingInventory = new Inventory(120, 540, 400, 9, 550);
        final Drink cappuccino = new Drink(CAPPU_BEANS, CAPPU_WATER, CAPPU_MILK, CAPPU_COST);
        final Drink latte = new Drink(LATTE_BEANS, LATTE_WATER, LATTE_MILK, LATTE_COST);
        final Drink espresso = new Drink(ESPR_BEANS, ESPR_WATER, 0, ESPR_COST);
        CoffeeMachine coffeeMachine = new CoffeeMachine(startingInventory);
        while (coffeeMachine.isPoweredOn()) {
            System.out.println("Write action (buy, fill, take, remaining, exit):");
            String mainMenuString = scanner.next();
            switch (mainMenuEnum.fromStr(mainMenuString)) {
                case REMAINING -> System.out.println(coffeeMachine.remainingMessage());
                case BUY -> {
                    System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
                    String buyMenuString = scanner.next();
                    buyMenuEnum buyMenuOption = buyMenuEnum.fromStr(buyMenuString);
                    try {
                        switch (buyMenuOption) {
                            case CAPPUCCINO -> {
                                coffeeMachine.inventory = coffeeMachine.inventory.update(cappuccino);
                                System.out.println("I have enough resources, making you a coffee!");
                            }
                            case LATTE -> {
                                coffeeMachine.inventory = coffeeMachine.inventory.update(latte);
                                System.out.println("I have enough resources, making you a coffee!");
                            }
                            case ESPRESSO -> {
                                coffeeMachine.inventory = coffeeMachine.inventory.update(espresso);
                                System.out.println("I have enough resources, making you a coffee!");
                            }
                            case BACK -> {}
                            default -> throw new Exception("invalid selection");
                        }
                    } catch(Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                case FILL -> {
                    System.out.println("Write how many ml of water you want to add:");
                    coffeeMachine.inventory.water = coffeeMachine.inventory.water.fill(scanner.nextInt());
                    System.out.println("Write how many ml of milk you want to add:");
                    coffeeMachine.inventory.milk = coffeeMachine.inventory.milk.fill(scanner.nextInt());
                    System.out.println("Write how many grams of coffee beans you want to add:");
                    coffeeMachine.inventory.beans = coffeeMachine.inventory.beans.fill(scanner.nextInt());
                    System.out.println("Write how many disposable cups you want to add:");
                    coffeeMachine.inventory.cups = coffeeMachine.inventory.cups.fill(scanner.nextInt());
                }
                case TAKE -> {
                    System.out.println("I gave you $" + coffeeMachine.inventory.money.getValue());
                    coffeeMachine.inventory.money = coffeeMachine.inventory.money.take();
                }
                case EXIT -> coffeeMachine.powerOff();
                default -> {
                }
            }
        }
    }
    enum mainMenuEnum {
        REMAINING, BUY, FILL, TAKE, EXIT, INVALID;
        static mainMenuEnum fromStr(String input) {
            try {
                return mainMenuEnum.valueOf(input.toUpperCase());
            } catch (Exception e) {
                return mainMenuEnum.INVALID;
            }
        }
    }
    enum buyMenuEnum {
        ESPRESSO, LATTE, CAPPUCCINO, BACK, INVALID;
        static buyMenuEnum fromStr(String input) {
            if ("back".equals(input)) {
                return matchInput(input);
            }
            try {
                int buyOptionInt = Integer.parseInt(input);
                return matchInput(buyOptionInt);
            } catch (Exception e) {
                return buyMenuEnum.INVALID;
            }
        }
        private static buyMenuEnum matchInput(int input) {
            return switch (input) {
                case 1 -> buyMenuEnum.ESPRESSO;
                case 2 -> buyMenuEnum.LATTE;
                case 3 -> buyMenuEnum.CAPPUCCINO;
                default -> buyMenuEnum.INVALID;
            };
        }
        private static buyMenuEnum matchInput(String input) {
            if ("back".equals(input)) {
                return buyMenuEnum.BACK;
            }
            return buyMenuEnum.INVALID;
        }
    }
}
class CoffeeConstants {
    static final int ESPR_BEANS = 16, LATTE_BEANS = 20, CAPPU_BEANS = 12,
            ESPR_WATER = 250, LATTE_WATER = 350, CAPPU_WATER = 200,
            ESPR_COST = 4, LATTE_COST = 7, CAPPU_COST = 6,
            LATTE_MILK = 75, CAPPU_MILK = 100;
}
class CoffeeMachine {
    Inventory inventory;
    private boolean poweredOn;
    public CoffeeMachine(Inventory inventory) {
        this.inventory = inventory;
        this.poweredOn = true;
    }
    public boolean isPoweredOn() {
        return this.poweredOn;
    }
    public void powerOff() {
        this.poweredOn = false;
    }
    public String remainingMessage() {
        return "The coffee machine has:\n" +
                this.inventory.water.getValue() + " ml of water\n" +
                this.inventory.milk.getValue() + " ml of milk\n" +
                this.inventory.beans.getValue() + " g of coffee beans\n" +
                this.inventory.cups.getValue() + " disposable cups\n" +
                "$" + this.inventory.money.getValue() + " of money";
    }
}
class Resource {
    public int value;
    public int getValue() {
        return value;
    }
    Resource(int value) {
        this.value = value;
    }
    public Resource fill(int value) {
        return new Resource(this.value + value);
    }
    public Resource take() {
        return new Resource(0);
    }
}
record Drink(int beans, int water, int milk, int price) {
}
class Inventory {
    Resource beans;
    Resource milk;
    Resource water;
    Resource cups;
    Resource money;
    Inventory(int beans, int milk, int water, int cups, int money) {
        this.beans = new Resource(beans);
        this.milk = new Resource(milk);
        this.water = new Resource(water);
        this.cups = new Resource(cups);
        this.money = new Resource(money);
    }
    public Inventory update(Drink drink) throws Exception {
        if (this.beans.getValue() < drink.beans()) {
            throw new Exception("Sorry, not enough beans!");
        }
        if (this.milk.getValue() < drink.milk()) {
            throw new Exception("Sorry, not enough milk!");
        }
        if (this.water.getValue() < drink.water()) {
            throw new Exception("Sorry, not enough water!");
        }
        if (this.cups.getValue() < 1) {
            throw new Exception("Sorry, not enough cups!");
        }
        return new Inventory(
                this.beans.getValue() - drink.beans(),
                this.milk.getValue() - drink.milk(),
                this.water.getValue() - drink.water(),
                this.cups.getValue() - 1,
                this.money.getValue() + drink.price());
    }
}