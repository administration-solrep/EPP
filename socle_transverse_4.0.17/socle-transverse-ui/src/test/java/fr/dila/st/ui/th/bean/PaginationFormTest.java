package fr.dila.st.ui.th.bean;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaginationFormTest {
    private PaginationForm paginationForm;

    @Before
    public void before() {
        paginationForm = new PaginationForm();
    }

    @Test
    public void testSetSize() {
        /* Valeur négative => valeur par défaut */
        paginationForm.setSize("-10");
        assertThat(paginationForm.getSize()).isEqualTo(PaginationForm.DEFAULT_SIZE);

        /* Valeur 0 => valeur par défaut */
        paginationForm.setSize("0");
        assertThat(paginationForm.getSize()).isEqualTo(PaginationForm.DEFAULT_SIZE);

        /* Valeur non présente dans la liste des valeurs acceptées => valeur par défaut */
        paginationForm.setSize("120");
        assertThat(paginationForm.getSize()).isEqualTo(PaginationForm.DEFAULT_SIZE);

        /* Valeur nulle => valeur par défaut */
        paginationForm.setSize(null);
        assertThat(paginationForm.getSize()).isEqualTo(PaginationForm.DEFAULT_SIZE);

        /* Valeur dans la liste dees valeurs acceptées => ok */
        paginationForm.setSize("50");
        assertThat(paginationForm.getSize()).isEqualTo(PaginationForm.SIZE_50);
    }

    @Test
    public void testSetPage() {
        /* Valeur négative => valeur par défaut */
        paginationForm.setPage("-10");
        assertThat(paginationForm.getPage()).isEqualTo(PaginationForm.DEFAULT_PAGE);

        /* Valeur 0 => valeur par défaut */
        paginationForm.setPage("0");
        assertThat(paginationForm.getPage()).isEqualTo(PaginationForm.DEFAULT_PAGE);

        /* Valeur nulle => ok */
        paginationForm.setPage((Integer) null);
        assertThat(paginationForm.getPage()).isNull();

        paginationForm.setPage((String) null);
        assertThat(paginationForm.getPage()).isNull();

        /* Valeur positive => ok */
        paginationForm.setPage("100");
        assertThat(paginationForm.getPage()).isEqualTo(100);
    }

    @Test
    public void testGetStartElement() {
        /* Comportement standard */
        paginationForm.setSize(PaginationForm.DEFAULT_SIZE);
        paginationForm.setPage(2);
        assertThat(paginationForm.getStartElement()).isEqualTo(10);

        /* page size = null => on renvoie tjs 0 */
        paginationForm.setPage((Integer) null);
        assertThat(paginationForm.getStartElement()).isEqualTo(0);

        paginationForm.setPage(121);
        assertThat(paginationForm.getStartElement()).isEqualTo(1200);

        paginationForm.setSize(PaginationForm.SIZE_50);
        paginationForm.setPage(5);
        assertThat(paginationForm.getStartElement()).isEqualTo(200);
    }

    @Test
    public void testGetNbPage() {
        paginationForm.setSize(PaginationForm.DEFAULT_SIZE);
        paginationForm.setPage(4);

        /* Valeur négative => valeur par défaut */
        assertThat(paginationForm.getNbPage(-5)).isEqualTo(0);

        /* Valeur ok */
        assertThat(paginationForm.getNbPage(2)).isEqualTo(1);
        assertThat(paginationForm.getNbPage(63)).isEqualTo(7);

        paginationForm.setSize(PaginationForm.SIZE_50);

        assertThat(paginationForm.getNbPage(50)).isEqualTo(1);
        assertThat(paginationForm.getNbPage(103)).isEqualTo(3);
    }

    @Test
    public void testGetDefaultSize() {
        assertThat(paginationForm.getDefaultSize()).isEqualTo(10);
    }
}
