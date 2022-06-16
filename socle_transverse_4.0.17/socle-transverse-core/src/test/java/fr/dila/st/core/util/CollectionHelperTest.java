package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;

public class CollectionHelperTest {

    @Test
    public void testAsSortedList() {
        String str1 = "A : Une chaine";
        String str2 = "B : pour des tests ";
        String str3 = "C : pour dédé ";
        String str4 = "D : pour manger";

        List<String> unsortedList = ImmutableList.of(str4, str2, str1, str3);

        // On vérifie que la liste est bien desordonnée :
        assertThat(unsortedList).containsExactly(str4, str2, str1, str3);

        List<String> sortedList1 = CollectionHelper.asSortedList(unsortedList);

        assertThat(sortedList1).containsExactly(str1, str2, str3, str4);
    }

    @Test
    public void testAsSortedListWithComparator() {
        Personne personne1 = new Personne("pers1", 29);
        Personne personne2 = new Personne("pers2", 5);
        Personne personne3 = new Personne("pers3", 37);
        Personne personne4 = new Personne("pers4", 23);

        List<Personne> unsortedList = ImmutableList.of(personne1, personne2, personne3, personne4);

        // On vérifie que la liste est bien desordonnée :
        assertThat(unsortedList).containsExactly(personne1, personne2, personne3, personne4);

        List<Personne> sortedList1 = CollectionHelper.asSortedList(
            unsortedList,
            Comparator.comparingInt(Personne::getAge)
        );

        assertThat(sortedList1).containsExactly(personne2, personne4, personne1, personne3);
    }

    private class Personne {
        private String nom;
        private int age;

        public Personne(String nom, int age) {
            this.nom = nom;
            this.age = age;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
