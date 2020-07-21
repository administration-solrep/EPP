--réparer les evenements deja creer avant la correction de bug 0048704: l'evenement EVT39 ne dois pas avoir un champ copie
update evenement e set e.destinataireCopieConcat = null
where e.typeevenement = 'EVT39';
commit;
