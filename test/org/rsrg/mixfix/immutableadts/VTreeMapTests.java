package org.rsrg.mixfix.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;

public final class VTreeMapTests {

    @Test void emptyMapShouldHaveSizeZero() {
        var map = VTreeMap.<Integer, String>empty();
        Assertions.assertEquals(0, map.size());
    }

    @Test void emptyMapLookupShouldReturnNone() {
        var map = VTreeMap.<Integer, String>empty();
        var result = map.lookup(1);
        Assertions.assertEquals(Maybe.none(), result);
    }

    @Test void emptyMapMemberShouldReturnFalse() {
        var map = VTreeMap.<Integer, String>empty();
        var result = map.member(1);
        Assertions.assertFalse(result);
    }

    @Test void singleInsertionShouldIncreaseSizeToOne() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one");
        Assertions.assertEquals(1, updatedMap.size());
    }

    @Test void singleInsertionLookupShouldReturnValue() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one");
        var result = updatedMap.lookup(1);
        Assertions.assertEquals(Maybe.of("one"), result);
    }

    @Test void singleInsertionMemberShouldReturnTrue() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one");
        var result = updatedMap.member(1);
        Assertions.assertTrue(result);
    }

    @Test void multipleInsertionsShouldReflectCorrectSize() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(3,
                "three");
        Assertions.assertEquals(3, updatedMap.size());
    }

    @Test void multipleInsertionsLookupShouldReturnCorrectValues() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(3,
                "three");
        var result1 = updatedMap.lookup(1);
        var result2 = updatedMap.lookup(2);
        var result3 = updatedMap.lookup(3);
        Assertions.assertEquals(Maybe.of("one"), result1);
        Assertions.assertEquals(Maybe.of("two"), result2);
        Assertions.assertEquals(Maybe.of("three"), result3);
    }

    @Test void multipleInsertionsMemberShouldReturnTrueForExistingKeys() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(3,
                "three");
        var result1 = updatedMap.member(1);
        var result2 = updatedMap.member(2);
        var result3 = updatedMap.member(3);
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);
        Assertions.assertTrue(result3);
    }

    @Test void duplicateKeyInsertionShouldNotIncreaseSize() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(1, "uno");
        Assertions.assertEquals(1, updatedMap.size());
    }

    @Test void duplicateKeyInsertionShouldUpdateValue() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(1, "uno");
        var result = updatedMap.lookup(1);
        Assertions.assertEquals(Maybe.of("uno"), result);
    }

    @Test void lookupNonExistingKeyShouldReturnNone() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one");
        var result = updatedMap.lookup(2);
        Assertions.assertEquals(Maybe.none(), result);
    }

    @Test void memberNonExistingKeyShouldReturnFalse() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one");
        var result = updatedMap.member(2);
        Assertions.assertFalse(result);
    }

    /*@Test void toListShouldReturnAllKeyValuePairs() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(3,
                "three");
        var list = updatedMap.toList();
        var expectedList = new VList<Pair<Integer, String>>()
                .append(Pair.of(1, "one"))
                .append(Pair.of(2, "two"))
                .append(Pair.of(3, "three"));
        Assertions.assertEquals(expectedList, list);
    }

    @Test void toListShouldReflectInsertionsInOrder() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(3, "three").insert(1, "one").insert(2,
                "two");
        var list = updatedMap.toList();
        var expectedList =
                new VList<Pair<Integer, String>>().append(Pair.of(1, "one")).append(Pair.of(2, "two")).append(Pair.of(3, "three"));
        Assertions.assertEquals(expectedList, list);
    }*/

    @Test void toStringShouldReturnCorrectRepresentation() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(3,
                "three");
        var stringRepresentation = updatedMap.toString();
        var expectedString = "[(1, one), (2, two), (3, three)]";
        Assertions.assertEquals(expectedString, stringRepresentation);
    }

    @Test void toStringEmptyMapShouldReturnEmptyList() {
        var map = VTreeMap.<Integer, String>empty();
        var stringRepresentation = map.toString();
        var expectedString = "[]";
        Assertions.assertEquals(expectedString, stringRepresentation);
    }

    @Test void insertMultipleKeysAndVerifySize() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(5, "five").insert(3, "three").insert(7,
                "seven").insert(2, "two").insert(4, "four").insert(6, "six").insert(8, "eight");
        Assertions.assertEquals(7, updatedMap.size());
    }

    @Test void insertMultipleKeysAndVerifyLookups() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(5, "five") //
                .insert(3, "three") //
                .insert(7, "seven") //
                .insert(2, "two") //
                .insert(4, "four") //
                .insert(6, "six") //
                .insert(8, "eight");
        Assertions.assertEquals(Maybe.of("five"), updatedMap.lookup(5));
        Assertions.assertEquals(Maybe.of("three"), updatedMap.lookup(3));
        Assertions.assertEquals(Maybe.of("seven"), updatedMap.lookup(7));
        Assertions.assertEquals(Maybe.of("two"), updatedMap.lookup(2));
        Assertions.assertEquals(Maybe.of("four"), updatedMap.lookup(4));
        Assertions.assertEquals(Maybe.of("six"), updatedMap.lookup(6));
        Assertions.assertEquals(Maybe.of("eight"), updatedMap.lookup(8));
    }

    @Test void insertMultipleKeysAndVerifyMembers() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(10, "ten").insert(20, "twenty").insert(30
                , "thirty");
        Assertions.assertTrue(updatedMap.member(10));
        Assertions.assertTrue(updatedMap.member(20));
        Assertions.assertTrue(updatedMap.member(30));
        Assertions.assertFalse(updatedMap.member(40));
    }

    @Test void insertAndDeleteKeyShouldReflectCorrectSize() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(1, "uno");
        Assertions.assertEquals(2, updatedMap.size());
    }

    @Test void insertAndDeleteKeyShouldUpdateValue() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(1, "uno");
        var result = updatedMap.lookup(1);
        Assertions.assertEquals(Maybe.of("uno"), result);
    }

    @Test void insertNullValueShouldHandleGracefully() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, null);
        var result = updatedMap.lookup(1);
        Assertions.assertEquals(Maybe.of(null), result);
        Assertions.assertTrue(updatedMap.member(1));
        Assertions.assertEquals(1, updatedMap.size());
    }

    @Test void insertMultipleKeysWithSameValueShouldHandleCorrectly() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "same").insert(2, "same").insert(3,
                "same");
        Assertions.assertEquals(3, updatedMap.size());
        Assertions.assertEquals(Maybe.of("same"), updatedMap.lookup(1));
        Assertions.assertEquals(Maybe.of("same"), updatedMap.lookup(2));
        Assertions.assertEquals(Maybe.of("same"), updatedMap.lookup(3));
    }
/*
    @Test void toListAfterDuplicateInsertionsShouldReflectLatestValues() {
        var map = VTreeMap.<Integer, String>empty();
        var updatedMap = map.insert(1, "one").insert(2, "two").insert(1, "uno"
        ).insert(3, "three").insert(2, "dos");
        var list = updatedMap.toList();
        var expectedList =
                new VList<Pair<Integer, String>>().append(Pair.of(1, "uno")).append(Pair.of(2, "dos")).append(Pair.of(3, "three"));
        Assertions.assertEquals(expectedList, list);
    }*/
}
