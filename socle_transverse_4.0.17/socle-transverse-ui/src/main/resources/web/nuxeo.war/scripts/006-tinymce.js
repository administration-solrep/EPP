window.initRichText = function () {
    if ($("#format").length > 0) {
        var format = $("#format").val();
        changeEditorType(format);
    } else {
        initTinyMCE();
    }
};

function removeTinyMCE(id = "mce") {
    // Remove instance by id if exists
    if (tinymce.get(id)) {
        tinymce.remove("#" + id);
    }
}

function changeEditorType(value) {
    removeTinyMCE(); // Remove tinyMCE if exists

    if (value == "html") {
        // Add tinyMCE only for html format
        initTinyMCE();
    }
}

var tinyMCEDefaults = {
    plugins: "paste fullscreen table help",
    menu: {
        file: { title: "File", items: "newdocument restoredraft | preview | print " },
        edit: { title: "Edit", items: "undo redo | cut copy paste | selectall | searchreplace" },
        view: {
            title: "View",
            items: "code | visualaid visualchars visualblocks | spellchecker | preview fullscreen",
        },
        insert: {
            title: "Insert",
            items:
                "image link media template codesample inserttable | charmap emoticons hr | pagebreak nonbreaking anchor toc | insertdatetime",
        },
        format: { title: "Format", items: "superscript subscript" },
        table: { title: "Table", items: "inserttable | cell row column | tableprops deletetable" },
        help: { title: "Help", items: "help" },
    },
    toolbar: false,
    height: 400,
    importcss_append: true,
    a11y_advanced_options: true,
    language: "fr_FR",
    setup: function (editor) {
        editor.on("init", function () {
            initBorderTinyMCE(editor);
        });
    },
    // permet d'empêcher de copier/coller une image dans l'éditeur
    paste_preprocess: function (plugin, args) {
        if (args.content.startsWith("<img")) {
            data.content = "";
        }
    },
};

function initBorderTinyMCE(editor) {
    editor.getContainer().style.borderColor = "#8e8e8e";
    editor.getContainer().style.borderRadius = "5px";
}

function initTinyMCE(options = { selector: "textarea#mce" }) {
    var tinyMCEOptions = $.extend({}, tinyMCEDefaults, options);

    window.tinyMCE.init(tinyMCEOptions);
}
