package com.intelladept.domainiser.example;

import java.util.*;

/**
 * Example domain obejct
 *
 * @author Aditya Bhardwaj
 */
public class Person {
    private String name;
    private int age;
    private Person spouse;
    private List<Person> children = new ArrayList<Person>();
    private Set<Address> addresses   = new HashSet<Address>();
    private Map<String, Person> friends = new HashMap<String, Person>();
    private Address home;
    private Address office;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public void addChild(Person child) {
        this.children.add(child);
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public Map<String, Person> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Person> friends) {
        this.friends = friends;
    }

    public void addFriend(String name, Person friend) {
        this.friends.put(name, friend);
    }

    public Address getHome() {
        return home;
    }

    public void setHome(Address home) {
        this.home = home;
    }

    public Address getOffice() {
        return office;
    }

    public void setOffice(Address office) {
        this.office = office;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
        if (spouse != null && spouse.getSpouse() == null) {
            spouse.setSpouse(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }
}
