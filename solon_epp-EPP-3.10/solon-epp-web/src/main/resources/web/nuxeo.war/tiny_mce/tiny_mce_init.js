	function tinyMCEOnChangeCallback() {
		isDossierModified = true;
	}
	

    function toggleTinyMCE(id) {
      if (!tinyMCE.getInstanceById(id))
        addTinyMCE(id);
       else
        removeTinyMCE(id);
      }

    function removeTinyMCE(id) {
     tinyMCE.execCommand('mceRemoveControl', false, id);
    }

    function addTinyMCE(id) {
     tinyMCE.execCommand('mceAddControl', false, id);
    }