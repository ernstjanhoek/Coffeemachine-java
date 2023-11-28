package machine;

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
    private int value;
    private final String name;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
    public void checkResource(int limit) throws ResourceException {
        if (limit > this.value) {
            throw new ResourceException(this.getName());
        }
    }
    Resource(int value, String name) {
        this.value = value;
        this.name = name;
    }
    public void fill(int value) {
        this.value = this.value + value;
    }
    public void take() {
        this.value = 0;
    }
}
record Drink(int beans, int water, int milk, int price, int cups) {
}
class Inventory {
    Resource beans;
    Resource milk;
    Resource water;
    Resource cups;
    Resource money;
    Inventory(int beans, int milk, int water, int cups, int money) {
        this.beans = new Resource(beans, "beans");
        this.milk = new Resource(milk, "milk");
        this.water = new Resource(water, "water");
        this.cups = new Resource(cups, "cups");
        this.money = new Resource(money, "money");
    }
    public Inventory update(Drink drink) throws ResourceException {
        this.beans.checkResource(drink.beans());
        this.milk.checkResource(drink.milk());
        this.water.checkResource(drink.water());
        this.cups.checkResource(drink.cups());
        return new Inventory(
                this.beans.getValue() - drink.beans(),
                this.milk.getValue() - drink.milk(),
                this.water.getValue() - drink.water(),
                this.cups.getValue() - 1,
                this.money.getValue() + drink.price());
    }
}
class ResourceException extends Exception {
    String name;
    ResourceException(String name) {
        this.name = name;
    }
    @Override
    public String getMessage() {
        return "Sorry, not enough " + this.name;
    }
}