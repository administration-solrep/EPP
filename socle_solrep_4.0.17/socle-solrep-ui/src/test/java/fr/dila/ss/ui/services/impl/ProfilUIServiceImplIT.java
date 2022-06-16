package fr.dila.ss.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fr.dila.ss.core.test.SolrepFeature;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.th.bean.FicheProfilForm;
import fr.dila.st.api.user.Profile;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, SolrepFeature.class })
@Deploy("fr.dila.ss.ui")
public class ProfilUIServiceImplIT {
    @Inject
    private CoreSession session;

    @Inject
    private ProfilUIService uiService;

    private SpecificContext context = new SpecificContext();

    @Before
    public void setUp() {
        context.setSession(session);
    }

    @Test
    public void testCreateProfil() {
        // Given
        FicheProfilForm form = new FicheProfilForm();
        form.setLabel("super-profil");
        form.setFonctionsId(Lists.newArrayList("super-function"));

        context.putInContextData(SSContextDataKey.PROFIL_FORM, form);

        // When
        uiService.createProfile(context);

        // Then
        context.putInContextData(STContextDataKey.PROFILE_ID, "super-profil");
        Profile profile = uiService.getProfilDoc(context).getAdapter(Profile.class);
        assertThat(profile).isNotNull();
        assertThat(profile.getBaseFunctionList()).containsExactly("super-function");
    }

    @Test
    public void testUpdateProfil() {
        // Given
        FicheProfilForm form = new FicheProfilForm();
        form.setLabel("super-profil");
        form.setFonctionsId(Lists.newArrayList("super-function"));

        context.putInContextData(SSContextDataKey.PROFIL_FORM, form);
        uiService.createProfile(context);

        form.setId("super-profil");
        form.setFonctionsId(Lists.newArrayList("super-function-2"));

        // When
        uiService.updateProfile(context);

        // Then
        context.putInContextData(STContextDataKey.PROFILE_ID, "super-profil");
        Profile profile = uiService.getProfilDoc(context).getAdapter(Profile.class);
        assertThat(profile).isNotNull();
        assertThat(profile.getBaseFunctionList()).containsExactly("super-function-2");
    }
}
