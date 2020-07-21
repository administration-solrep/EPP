-- Permet d'initialiser les valeurs par défaut de Solon EPP
-- Script pour la plateforme d'intégration (INTE)

-- URL de l'application transmise par mail
UPDATE parametre p SET p.value='http://idlv-solrep-epp-inte-01.lyon-dev2.local:8180/solon-epp' WHERE p.id=
	(SELECT h.id FROM hierarchy h WHERE h.primarytype='Parametre' AND h.name='url-application-transmise-par-mel');

-- Adresse mail administrateur
UPDATE parametre p SET p.value='Administration-SOLREP@solon-epp.com' WHERE p.id=
	(SELECT h.id FROM hierarchy h WHERE h.primarytype='Parametre' AND h.name='adresse-mail-administrateur-application');

-- Date de dernier changement de mot de passe
UPDATE profil_utilisateur_solon_epp SET dernierchangementmotdepasse = SYSDATE;
	
commit;
