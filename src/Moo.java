record Fruit(String title, int quantity) {

    @Override public String toString() {
        return String.format("Fruit(name=%s, quantity=%d)", title, quantity);
    }
}

public void main() {

    var fruits = new ArrayList<>();
    fruits.add(new Fruit("apple", 3));
    fruits.add(new Fruit("pear", 23));
    fruits.add(new Fruit("banana", 10));
    fruits.add(new Fruit("kiwi", 18));
    System.out.println(fruits);
}
