// Format de date par défaut
var DEFAULT_DATE_FORMAT = "dd/mm/yyyy";

// Définition du nombre de jours dans chacun des mois de l'année
var DAYS_PER_MONTH = function (year) {
    return [
        31,
        (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ? 29 : 28,
        31,
        30,
        31,
        30,
        31,
        31,
        30,
        31,
        30,
        31,
    ];
};

// Date JO min
var JO_DATE_MIN = new Date("1869", "0", "1");

// Validateurs (triés par ordre alphabétique)
var formValidators = {
    utils: {
        isValidDate: function (val, format) {
            format = typeof format == "string" ? format : DEFAULT_DATE_FORMAT;
            var dateParts = val.match(/(\d+)/g),
                i = 0,
                formatParts = {};
            format.replace(/(yyyy|dd|mm)/g, function (part) {
                formatParts[part] = i++;
            });

            return (
                DAYS_PER_MONTH(dateParts[formatParts["yyyy"]])[dateParts[formatParts["mm"]] - 1] &&
                dateParts[formatParts["dd"]] <=
                    DAYS_PER_MONTH(dateParts[formatParts["yyyy"]])[dateParts[formatParts["mm"]] - 1] &&
                dateParts[formatParts["dd"]] > 0
            );
        },
        isAfter: function (val, ref, egalTest) {
            if (val == "" || ref == "") {
                return true;
            }
            //format de dates dd/mm/yyyy
            let regEx = new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/);
            if (!regEx.test(val) || !regEx.test(ref)) {
                return true; //Pour ne pas afficher le message de comparaison des dates si format invalide => necessite de verifier le format par ailleurs
            }
            let refDateParts = ref.split("/");
            let valDateParts = val.split("/");
            let refDate = new Date(refDateParts[2], refDateParts[1] - 1, refDateParts[0]); // -1 car les mois commencent à 0
            let valDate = new Date(valDateParts[2], valDateParts[1] - 1, valDateParts[0]);

            return valDate > refDate || (egalTest && valDate.valueOf() == refDate.valueOf());
        },
    },
    alpha: {
        validationFunction: function (val, params) {
            return new RegExp(/^[a-zA-Z]+$/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir uniquement des lettres.";
        },
    },
    alpha_num: {
        validationFunction: function (val, params) {
            return new RegExp(/^[a-zA-Z0-9]+$/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir uniquement des lettres et des chiffres.";
        },
    },
    alpha_num_dash: {
        validationFunction: function (val, params) {
            return new RegExp(/^[a-zA-Z0-9-]+$/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir uniquement des lettres, des chiffres et des tirets.";
        },
    },
    does_not_start_with_asterisk: {
        validationFunction: function (val, params) {
            return new RegExp(/^(?![\*]).*$/).test(val);
        },
        message: function (params) {
            return "Le premier caractère ne doit pas être un astérisque.";
        },
    },
    alpha_num_space_asterisk: {
        validationFunction: function (val, params) {
            return new RegExp(/^[a-zA-Z0-9 \*]+$/).test(val);
        },
        message: function (params) {
            return "Caractères autorisés : lettres, chiffres, espaces, astérisques.";
        },
    },
    alpha_num_asterisk_semicolon: {
        validationFunction: function (val, params) {
            return new RegExp(/^[a-zA-Z0-9\*;]+$/).test(val);
        },
        message: function (params) {
            return "Caractères autorisés : lettres, chiffres, astérisques, points-virgules.";
        },
    },
    query_param: {
        validationFunction: function (val, params) {
            return !new RegExp(/%/).test(val);
        },
        message: function (params) {
            return "Le champ ne doit pas contenir de symbole pourcentage.";
        },
    },
    user_name: {
        validationFunction: function (val, params) {
            return new RegExp(/^[\w\-\.@]{8,}$/).test(val);
        },
        message: function (params) {
            return (
                "Le champ doit contenir au moins 8 caractères et " +
                "uniquement des lettres, des chiffres et les caractères spéciaux suivants : -_.@"
            );
        },
    },
    between: {
        validationFunction: function (val, params) {
            // params doit être au format [min, max], e.g. ["30", "50"]
            return (
                new RegExp(/^[0-9]+.?[0-9]*$/).test(val) && val >= parseFloat(params[0]) && val <= parseFloat(params[1])
            );
        },
        message: function (params) {
            return "Le champ doit être un nombre entier compris entre " + params[0] + " et " + params[1] + ".";
        },
    },
    checkbox_required: {
        validationFunction: function (val, params) {
            return val;
        },
        message: function (params) {
            return "Veuillez cocher cette case si vous souhaitez continuer.";
        },
    },
    required_date_unique_format: {
        validationFunction: function (val, params) {
            // params doit être au format [dateFormat], e.g. ["dd/mm/yyyy"]
            switch (params[0]) {
                case "yyyy-mm-dd":
                    return (
                        new RegExp(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/).test(val) &&
                        formValidators.utils.isValidDate(val, params[0])
                    );
                case DEFAULT_DATE_FORMAT:
                case "mm/dd/yyyy":
                default:
                    return (
                        new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(val) &&
                        formValidators.utils.isValidDate(val, params[0])
                    );
            }
        },
        message: function (params) {
            return (
                "Le champ doit être une date valide au format " +
                params[0].replace(/d/g, "j").replace(/y/g, "a") +
                ", par exemple 31/12/2018."
            );
        },
    },
    date_unique_format: {
        validationFunction: function (val, params) {
            // Si c'est vide c'est forcément valide
            if (val.length <= 0) {
                return true;
            }
            // params doit être au format [dateFormat], e.g. ["dd/mm/yyyy"]
            switch (params[0]) {
                case "yyyy-mm-dd":
                    return (
                        new RegExp(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/).test(val) &&
                        formValidators.utils.isValidDate(val, params[0])
                    );
                case DEFAULT_DATE_FORMAT:
                case "mm/dd/yyyy":
                default:
                    return (
                        new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(val) &&
                        formValidators.utils.isValidDate(val, params[0])
                    );
            }
        },
        message: function (params) {
            return (
                "Le champ doit être une date valide au format " +
                params[0].replace(/d/g, "j").replace(/y/g, "a") +
                ", par exemple 31/12/2018."
            );
        },
    },
    date_inferior_to: {
        validationFunction: function (val, params) {
            return formValidators.utils.isAfter(val, $("#" + params[0]).val(), false);
        },
        message: function (params) {
            return "La date de fin doit être postérieure à la date de début.";
        },
    },
    date_inferior_or_equal_to: {
        validationFunction: function (val, params) {
            return formValidators.utils.isAfter(val, $("#" + params[0]).val(), true);
        },
        message: function (params) {
            return "La date de fin doit être postérieure ou égale à la date de début.";
        },
    },
    date_superior_to: {
        validationFunction: function (val, params) {
            return formValidators.utils.isAfter($("#" + params[0]).val(), val, false);
        },
        message: function (params) {
            return "La date de début doit être antérieure à la date de fin.";
        },
    },
    date_superior_or_equal_to: {
        validationFunction: function (val, params) {
            return formValidators.utils.isAfter($("#" + params[0]).val(), val, true);
        },
        message: function (params) {
            return "La date de début doit être antérieure ou égale à la date de fin.";
        },
    },
    date_range_format: {
        validationFunction: function (val, params) {
            // Si c'est vide c'est forcément valide
            if (val.length <= 0) {
                return true;
            }

            // params doit être au format [dateFormat, datesSeparator], e.g.
            // ["dd/mm/yyyy", ">"]
            if (val.indexOf(params[1].trim()) !== -1) {
                var dates = val.split(params[1].trim()),
                    date1 = dates[0].trim(),
                    date2 = dates[1].trim();
            } else {
                return false;
            }

            switch (params[0]) {
                case "yyyy-mm-dd":
                    return (
                        new RegExp(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/).test(date1) &&
                        new RegExp(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/).test(date2) &&
                        formValidators.utils.isValidDate(date1, params[0]) &&
                        formValidators.utils.isValidDate(date2, params[0])
                    );
                case DEFAULT_DATE_FORMAT:
                case "mm/dd/yyyy":
                default:
                    return (
                        new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(date1) &&
                        new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(date2) &&
                        formValidators.utils.isValidDate(date1, params[0]) &&
                        formValidators.utils.isValidDate(date2, params[0])
                    );
            }
        },
        message: function (params) {
            return (
                "Le champ doit être une période de dates valide au format " +
                params[0] +
                " " +
                params[1] +
                " " +
                params[0] +
                "."
            );
        },
    },
    date_multiple_formats: {
        validationFunction: function (val, params) {
            // Si c'est vide c'est forcément valide
            if (val.length <= 0) {
                return true;
            }

            // params doit être au format [dateFormat, dateFormat,
            // dateFormat...], e.g. ["dd/mm/yyyy", "mm/yyyy", "yyyy"]
            var countValid = 0;
            for (var i in params) {
                if (params.hasOwnProperty(i)) {
                    var dateFormat = params[i];
                    switch (dateFormat) {
                        case DEFAULT_YEAR_FORMAT:
                        case "yyyy":
                            if (new RegExp(/^[0-9]{4}$/).test(val)) {
                                countValid++;
                            }
                            break;
                        case DEFAULT_MONTH_FORMAT:
                        case "mm/yyyy":
                            if (new RegExp(/^[0-9]{2}\/[0-9]{4}$/).test(val)) {
                                countValid++;
                            }
                            break;
                        case "yyyy-mm-dd":
                            if (
                                new RegExp(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/).test(val) &&
                                formValidators.utils.isValidDate(val, dateFormat)
                            ) {
                                countValid++;
                            }
                            break;
                        case DEFAULT_DATE_FORMAT:
                        case "mm/dd/yyyy":
                        default:
                            if (
                                new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(val) &&
                                formValidators.utils.isValidDate(val, dateFormat)
                            ) {
                                countValid++;
                            }
                            break;
                    }
                }
            }

            return countValid === 1;
        },
        message: function (params) {
            return (
                "Le champ doit être une période de dates valide au format " +
                params.join(" ou ").replace(/d/g, "j").replace(/y/g, "a") +
                ", par exemple 31/12/2018, 2018, ou 12/2018."
            );
        },
    },
    date_year_format: {
        validationFunction: function (val, params) {
            // params doit être au format [dateFormat], e.g. ["yyyy"]
            return new RegExp(/^[0-9]{4}$/).test(val);
        },
        message: function (params) {
            return "Le champ doit être une date valide au format " + params[0] + ".";
        },
    },
    date_month_format: {
        validationFunction: function (val, params) {
            // params doit être au format [dateFormat], e.g. ["mm/yyyy"]
            return new RegExp(/^[0-9]{2}\/[0-9]{4}$/).test(val);
        },
        message: function (params) {
            return "Le champ doit être une date valide au format " + params[0] + ".";
        },
    },
    date_min_jo: {
        validationFunction: function (val, params) {
            if (!val) {
                return true;
            }

            var dateAsString;
            if (val.indexOf(DEFAULT_DATE_RANGE_SEPARATOR_TRIM) !== -1) {
                dateAsString = val.split(DEFAULT_DATE_RANGE_SEPARATOR_TRIM)[0].trim();
            } else {
                dateAsString = val;
            }

            if (
                !new RegExp(/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/).test(dateAsString) ||
                !formValidators.utils.isValidDate(dateAsString, DEFAULT_DATE_FORMAT)
            ) {
                return true;
            }

            var dateParts = dateAsString.split(DEFAULT_DATE_FORMAT_SEPARATOR);
            var date = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);

            return date >= JO_DATE_MIN;
        },
        message: function (params) {
            return "Il n'y a pas de contenu disponible à la date indiquée. Veuillez indiquer une date à partir du 01/01/1869.";
        },
    },
    email: {
        validationFunction: function (val, params) {
            return new RegExp(
                /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            ).test(val);
        },
        message: function (params) {
            return "Une seule adresse mail valide est autorisée (ex. : nom@exemple.fr)";
        },
    },
    integer: {
        validationFunction: function (val, params) {
            return new RegExp(/^[0-9]*$/).test(val.toString());
        },
        message: function (params) {
            return "Le champ doit être un nombre entier positif.";
        },
    },
    max: {
        validationFunction: function (val, params) {
            // params doit être au format [max], e.g. ["50"]
            return !val || (new RegExp(/^[0-9]+.?[0-9]*$/).test(val) && val <= parseFloat(params[0]));
        },
        message: function (params) {
            return "Le champ doit être un nombre entier inférieur à " + params[0] + ".";
        },
    },
    min: {
        validationFunction: function (val, params) {
            // params doit être au format [min], e.g. ["30"]
            return !val || (new RegExp(/^[0-9]+.?[0-9]*$/).test(val) && val >= parseFloat(params));
        },
        message: function (params) {
            return "Le champ doit être un nombre entier supérieur à " + params[0] + ".";
        },
    },
    maxlength: {
        validationFunction: function (val, params) {
            return val.length <= params[0];
        },
        message: function (params) {
            return `Le champ doit contenir au plus ${params[0]} caractères.`;
        },
    },
    minlength: {
        validationFunction: function (val, params) {
            return val.length >= params[0];
        },
        message: function (params) {
            return `Le champ doit contenir au moins ${params[0]} caractères.`;
        },
    },
    numeric: {
        validationFunction: function (val, params) {
            return new RegExp(/^-?[0-9]+.?[0-9]*$/).test(val);
        },
        message: function (params) {
            return "Le champ doit être un nombre valide.";
        },
    },
    not_empty: {
        validationFunction: function (val, params) {
            return new RegExp(/([^\s])/).test(val);
        },
        message: function (params) {
            return "Le champ ne doit pas être vide.";
        },
    },
    phone_number: {
        validationFunction: function (val, params) {
            return new RegExp(/^(?:(?:\+|00)33|0)\s*[1-9](?:[\s\.-]*\d{2}){4}$/).test(val.trim());
        },
        message: function (params) {
            return "Le champ doit être un numéro de téléphone français valide.";
        },
    },
    generic_phone_number: {
        validationFunction: function (val, params) {
            return val.length <= 0 || new RegExp(/^[0-9](?:[\s\.-]*\d)+$/).test(val.trim());
        },
        message: function (params) {
            return "Le champ doit être un numéro de téléphone ou de poste valide.";
        },
    },
    required: {
        validationFunction: function (val, params) {
            return val != null && new RegExp(/([^\s])/).test(val);
        },
        message: function (params) {
            return "Le champ est requis.";
        },
    },
    multiple_list_selector: {
        validationFunction: function (val, params) {
            return $("#" + params[0]).find("li").length > 0;
        },
        message: function (params) {
            return "Le champ est requis.";
        },
    },
    required_select: {
        validationFunction: function (val, params) {
            return new RegExp(/([^\s])/).test(val);
        },
        message: function (params) {
            return "Le champ est requis.";
        },
    },
    one_digit: {
        validationFunction: function (val, params) {
            return new RegExp(/\d/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir au moins un chiffre.";
        },
    },
    one_special: {
        validationFunction: function (val, params) {
            return new RegExp(/[~`!@#$%^&*()_+={}\[\]|:;"'<>,.?\/-]/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir au moins un caractère spécial.";
        },
    },
    one_lower_case: {
        validationFunction: function (val, params) {
            return new RegExp(/[a-z]/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir au moins une lettre minuscule.";
        },
    },
    one_upper_case: {
        validationFunction: function (val, params) {
            return new RegExp(/[A-Z]/).test(val);
        },
        message: function (params) {
            return "Le champ doit contenir au moins une lettre majuscule.";
        },
    },
    reference_loi_ordonnance: {
        validationFunction: function (val, params) {
            return new RegExp(/^[0-9]{4}-[0-9]{1,4}$/).test(val.toString());
        },
        message: function (params) {
            return "Le champ doit être une référence valide.";
        },
    },
    numero_texte: {
        validationFunction: function (val, params) {
            return new RegExp(/^20[0-9]{2}-[0-9]+$/).test(val.toString());
        },
        message: function (params) {
            return "Le n° texte doit être valide";
        },
    },
    no_slash: {
        validationFunction: function (val, params) {
            return new RegExp(/^[^\/]+$/).test(val.toString());
        },
        message: function (params) {
            return "Le champ ne doit pas contenir le caractère suivant : /";
        },
    },
};

// Lorsque le DOM est prêt
$(document).ready(function () {
    // Initialisation de la validation
    initFormValidation();
});

/*******************************************************************************
 * Fonction de binding des formulaires à la validation de leurs champs
 ******************************************************************************/
function initFormValidation() {
    // Pour chacun des formulaires à valider
    $("form").submit(function () {
        return isValidForm($(this));
    });

    // Ajout d'un event "blur" pour chaque input, possédant l'attribut
    // "data-validation", à valider
    $("input[data-validation]").each(function () {
        var input = $(this);

        // Sauvegarde des descriptions aria
        if (input.attr("aria-describedby") !== undefined) {
            input.attr("data-describedby", input.attr("aria-describedby"));
        }

        input.blur(function (event) {
            isValidInput(input);
        });

        // On ajoute le même event sur le bouton adjacent à l'input
        // dans le cas d'un datepicker par exemple
        var inputButton = input.siblings("button");
        if (inputButton.length) {
            inputButton[0].addEventListener("hideDatepicker", function (e) {
                isValidInput(input);
            });
        }
    });

    $("select[data-validation]").each(function () {
        var input = $(this);

        // Sauvegarde des descriptions aria (sur le bon input enfant)
        if (input.parent().find("input").attr("aria-describedby") !== undefined) {
            input
                .parent()
                .find("input")
                .attr("data-describedby", input.parent().find("input").attr("aria-describedby"));
        }

        input.blur(function (event) {
            isValidInput(input);
        });

        input
            .next(".aria-autocomplete__wrapper")
            .find("input")
            .blur(function (event) {
                isValidInput(input);
            });
    });

    // Ajout d'un event "blur" pour chaque textarea, possédant l'attribut
    // "data-validation", à valider
    $("textarea[data-validation]").each(function () {
        $(this).blur(function (event) {
            isValidInput($(this));
        });
    });
}

/*******************************************************************************
 * Fonction de contrôle frontend d'un champ de formulaire. Le champ à valider
 * doit comporter un attribut 'data-validation' avec pour valeur la liste des
 * contrôles à effectuer.
 ******************************************************************************/
function isValidInput($input) {
    if ($input.data("validation") && !$input.attr("readonly")) {
        // Suppression des précédentes classes de succès ou d'erreurs
        cleanPreviousErrorOrSuccess($input);

        // Contrôle de tous les validateurs de l'input
        var errors = checkAllValidators($input);

        if (errors.length) {
            // Si il y a des erreurs, on les affiche
            constructErrors(errors, $input);
            return false;
        } else {
            // Si pas d'erreur, on affiche le succès
            constructSuccess($input);
            return true;
        }
    }
}

/*******************************************************************************
 * Fonction de contrôle frontend des champs de formulaire. Les champs à valider
 * doivent comporter un attribut 'data-validation' avec pour valeur la liste des
 * contrôles à effectuer.
 ******************************************************************************/
function isValidForm($form) {
    // Récupération de tous les inputs à valider
    var $inputsToValidate = $form.find(
        "input[data-validation]:enabled[readonly!='readonly'], textarea[data-validation]:enabled[readonly!='readonly'], select[data-validation]:enabled[readonly!='readonly']"
    );

    // Déclaration d'un tableau comportant les inputs non valides
    var notValidInputs = [];

    // Suppression de toutes les erreurs existantes avant affichage des
    // éventuelles nouvelles
    $(".form-input__description--error").remove();
    $(".form-textarea__description--error").remove();
    $(".icon--exclamation-point").remove();

    // Pour chacun des inputs à valider
    $inputsToValidate.each(function (index, input) {
        var $input = $(this);

        // S'il y a des erreurs pour l'input
        if (!isValidInput($input)) {
            // Ajout de l'input non valide au tableau des inputs non valides
            notValidInputs.push($input);
        }
    });

    // Si il y a au moins une erreur dans le formulaire
    if (notValidInputs.length) {
        //on ouvre l'accordéon s'il est fermé
        var accordion = notValidInputs[0].parents("div.accordion");
        if (accordion.length > 0) {
            var button = accordion.find(".js-toggle").get(0);
            if (button && button.getAttribute("aria-expanded") == "false") {
                var targetId = button.getAttribute("aria-controls");
                toggleDOMElement(targetId, button);
            }
        }

        //Dans le cas d'un select autocomplete on donne le focus à l'input
        if (notValidInputs[0].hasClass("form-select-input-autocomplete__field")) {
            notValidInputs[0].next(".aria-autocomplete__wrapper").find("input").focus();
        } else {
            notValidInputs[0].focus();
        }

        return false;
    } else {
        return true;
    }
}

/**
 * Fonction permettant de vérifier que le champ Tiny MCE n'est pas vide
 */
function isNotEmptyTinyMce(fieldName, idContent) {
    const content = tinymce.get("mce").getContent();
    // Suppression de toutes les erreurs existantes avant affichage des
    // éventuelles nouvelles
    $(".alert").remove();
    if (!content) {
        // si empty -> message d'erreur et on renvoie false
        constructAlert(errorMessageType, ['Le champ "' + fieldName + '" est requis'], idContent);
        if (idContent) {
            $("#focusAlert-" + idContent).focus();
        }
        return false;
    }

    return true;
}

/*******************************************************************************
 * Fonctions de suppression des classes d'erreur ou de succès
 ******************************************************************************/
function cleanAllPreviousErrorOrSuccess() {
    // Suppression de toutes les erreurs existantes
    $(".form-input__description--error").remove();
    $(".form-textarea__description--error").remove();
    $(".icon--exclamation-point").remove();
    $(".form-input__field--error").removeClass("form-input__field--error");
    $(".form-select-input__field--error").removeClass("form-select-input__field--error");
    $(".aria-autocomplete__wrapper").removeClass("aria-autocomplete__wrapper--error");
    $(".aria-autocomplete__input").removeClass("form-input__field--error");
    // Suppression du précédent état succès (bordure verte et icône)
    $(".bubble-icon--success").remove();
    $(".form-input__field--success").removeClass("form-input__field--success");
    $(".form-select-input__field--success").removeClass("form-select-input__field--success");
    $(".aria-autocomplete__wrapper").removeClass("aria-autocomplete__wrapper--success");
    $(".aria-autocomplete__input").removeClass("form-input__field--success");
}

function cleanPreviousErrorOrSuccess($input) {
    // pour le date-picker-range, on n'efface que la description du champ associé
    if ($input.hasClass("date-picker-range")) {
        var widget = $input.parent().hasClass("form-date-interval__beginning") ? "left" : "right";

        $input
            .closest(".form-input")
            .children(".form-input__footer")
            .children(".date-interval__description--" + widget)
            .children(".form-input__description--error")
            .remove();

        $input.removeClass("form-input__field--success");
        $input.removeClass("form-input__field--error");
        $input
            .closest(".form-input")
            .children(".form-input__header")
            .children(".form-input__header-top")
            .children(".form-input__icon-group")
            .children(".date-interval__icons--" + widget)
            .empty();
    }

    // Suppression de toutes les erreurs existantes
    else if (
        $input.hasClass("form-input__field--error") ||
        $input.hasClass("form-select-input__field--error") ||
        $input.hasClass("form-date-picker-input__form-input--error")
    ) {
        $input.removeClass("form-input__field--error");
        $input.removeClass("form-select-input__field--error");
        $input.removeClass("form-date-picker-input__form-input--error");
        $input
            .closest(".form-input")
            .children(".form-input__footer")
            .children(".form-input__description--error")
            .remove();
        $input
            .closest(".form-input")
            .children(".form-input__header")
            .children(".form-input__header-top")
            .children(".form-input__icon-group")
            .children(".icon--exclamation-point")
            .remove();
        if ($input.hasClass("form-select-input-autocomplete__field")) {
            const wrapper = $input.next(".aria-autocomplete__wrapper");
            wrapper.removeClass("aria-autocomplete__wrapper--error");
            wrapper.children(".aria-autocomplete__input").removeClass("form-input__field--error");
        }
    } else if ($input.hasClass("form-input__field--success") || $input.hasClass("form-select-input__field--success")) {
        // Suppression du précédent état succès (bordure verte et icône)
        $input.removeClass("form-input__field--success");
        $input.removeClass("form-select-input__field--success");
        $input
            .closest(".form-input")
            .children(".form-input__header")
            .children(".form-input__header-top")
            .children(".form-input__icon-group")
            .children(".bubble-icon--success")
            .remove();

        if ($input.hasClass("form-select-input-autocomplete__field")) {
            const wrapper = $input.next(".aria-autocomplete__wrapper");
            wrapper.removeClass("aria-autocomplete__wrapper--success");
            wrapper.children(".aria-autocomplete__input").removeClass("form-input__field--success");
        }
    } else if ($input.hasClass("form-optin__checkbox")) {
        $input.closest(".form-optin").removeClass("form-optin--error");
    }

    if ($input.hasClass("form-select-input-autocomplete__field")) {
        $input = $input.parent().find("input");
    }

    $input.removeAttr("aria-describedby");
    $input.closest(".grid__row").css("align-items", "align-end");

    // Réaffectation des descriptions aria
    if ($input.attr("data-describedby") !== undefined) {
        $input.attr("aria-describedby", $input.attr("data-describedby"));
    }
}

/*******************************************************************************
 * Fonction de validation d'un champ par rapport à tous ses validateurs
 ******************************************************************************/
function checkAllValidators($input) {
    // Récupération de la valeur du champ
    var inputValue = $input.val();
    if ($input.attr("type") === "checkbox") {
        inputValue = $input.prop("checked");
    }
    // Récupération de la liste des validateurs à appliquer pour ce champ
    var validators = $input.data("validation").split(",");

    // Définition des erreurs pour ce champ
    var inputErrors = [];

    // Pour chaque validateur
    $(validators).each(function (index, validator) {
        validator = validator.trim();
        error = checkValidator(inputValue, validator);

        if (error != null) {
            inputErrors.push(error);
        }
    });

    return inputErrors;
}

/*******************************************************************************
 * Fonction de validation d'un champ par rapport à un validateur
 ******************************************************************************/
function checkValidator(value, validator) {
    var params = [];
    var bracketIndex = validator.indexOf("(");
    if (bracketIndex != -1) {
        params = validator.substring(bracketIndex + 1, validator.length - 1).split(";");
        validator = validator.substring(0, bracketIndex);
    }
    if (!formValidators[validator].validationFunction(value, params)) {
        return formValidators[validator].message(params);
    } else {
        return null;
    }
}

function cleanElement(formIconGroup, descriptionDiv) {
    formIconGroup.children(".bubble-icon--success,.bubble-icon--danger, .sr-only").remove();
    descriptionDiv.children(".field-error-tag,.field-success-tag").remove();
}

function constructSuccess(inputElement) {
    var inputElm = inputElement;
    // Bordure de l'input en vert
    if (inputElement.is("select")) {
        inputElm = inputElement.parent().find("input");
        if (inputElement.hasClass("form-select-input-autocomplete__field")) {
            const wrapper = inputElement.next(".aria-autocomplete__wrapper");
            wrapper.addClass("aria-autocomplete__wrapper--success");
            wrapper.children(".aria-autocomplete__input").addClass("form-input__field--success");
        }
        inputElement.addClass("form-select-input__field--success");
    } else {
        inputElement.addClass("form-input__field--success");
    }

    // Afficher l'icone de succès
    var successIcon = initializeSuccessIcon();
    var descriptionDiv = inputElement.closest(".form-input").children(".form-input__footer");
    var formIconGroup = inputElement
        .closest(".form-input")
        .children(".form-input__header")
        .children(".form-input__header-top")
        .children(".form-input__icon-group");

    if (inputElement.hasClass("date-picker-range")) {
        var widget = inputElement.parent().hasClass("form-date-interval__beginning") ? "left" : "right";
        descriptionDiv = descriptionDiv.children(".date-interval__description--" + widget);
        formIconGroup = formIconGroup.children(".date-interval__icons--" + widget);
    }

    var hadErrors = descriptionDiv.children(".field-error-tag").length != 0;

    cleanElement(formIconGroup, descriptionDiv);

    formIconGroup.append(successIcon);

    if (hadErrors) {
        formIconGroup.append('<span class="sr-only">Champ corrigé</span>');
        descriptionDiv.prepend('<span class="sr-only field-success-tag">Champ corrigé</span>');
        if (inputElm.attr("data-describedby") !== undefined) {
            inputElm.attr("aria-describedby", inputElm.attr("data-describedby") + " " + descriptionDiv.attr("id"));
        } else {
            inputElm.attr("aria-describedby", descriptionDiv.attr("id"));
        }
    }
}

function constructErrors(errors, inputElement) {
    var descriptionDiv = inputElement.closest(".form-input").children(".form-input__footer");
    var formIconGroup = inputElement
        .closest(".form-input")
        .children(".form-input__header")
        .children(".form-input__header-top")
        .children(".form-input__icon-group");

    if (inputElement.hasClass("date-picker-range")) {
        var widget = inputElement.parent().hasClass("form-date-interval__beginning") ? "left" : "right";
        descriptionDiv = descriptionDiv.children(".date-interval__description--" + widget);
        formIconGroup = formIconGroup.children(".date-interval__icons--" + widget);
    }

    if (inputElement.hasClass("form-file__input")) {
        formIconGroup = inputElement
            .closest(".form-file")
            .children(".form-input__header-top")
            .children(".form-input__icon-group");
        descriptionDiv = inputElement.closest(".form-file__container").children(".form-file-input__footer");
    }

    if (inputElement.hasClass("form-optin__checkbox")) {
        descriptionDiv = inputElement.closest(".form-optin__fieldset").find(".form-input__footer");
    }

    var errorDiv = initializeErrorDiv();

    if (errors.length === 1) {
        errorDiv.text(errors[0]);
    } else {
        errorDiv.append(buildErrorList(errors));
        descriptionDiv.css("display", "contents");
        inputElement.closest(".grid__row").css("align-items", "flex-start");
    }

    // afficher le(s) message(s) d'erreurs
    descriptionDiv.prepend(errorDiv);

    var inputElm = inputElement;

    // contour de l'input en rouge
    if (inputElement.is("select")) {
        inputElm = inputElement.parent().find("input");
        if (inputElement.hasClass("form-select-input-autocomplete__field")) {
            const wrapper = inputElement.next(".aria-autocomplete__wrapper");
            wrapper.addClass("aria-autocomplete__wrapper--error");
            wrapper.children(".aria-autocomplete__input").addClass("form-input__field--error");
        }
        inputElement.addClass("form-select-input__field--error");
    } else if (inputElement.is(":radio")) {
        inputElement.closest(".form-optin").addClass("form-optin--error");
    } else {
        inputElement.addClass("form-input__field--error");
    }

    cleanElement(formIconGroup, descriptionDiv);

    // afficher l'icone d'erreur
    var errorIcon = initializeErrorIcon();
    formIconGroup.append(errorIcon);

    descriptionDiv.prepend('<span class="sr-only field-error-tag">Erreur sur le champ</span>');

    if (inputElm.attr("data-describedby") !== undefined) {
        inputElm.attr("aria-describedby", inputElm.attr("data-describedby") + " " + descriptionDiv.attr("id"));
    } else {
        inputElm.attr("aria-describedby", descriptionDiv.attr("id"));
    }

    return errors;
}
function initializeErrorDiv() {
    var errorDiv = $("<div></div>")
        .addClass("form-input__description")
        .addClass("form-input__description--default")
        .addClass("form-input__description--error");
    return errorDiv;
}

function initializeSuccessIcon() {
    var successSpan = $('<span aria-hidden="true"></span>')
        .addClass("form-input__icon-item")
        .addClass("icon icon--check")
        .addClass("bubble-icon bubble-icon--success");
    return successSpan;
}

function initializeErrorIcon() {
    var errorSpan = $('<span aria-hidden="true"></span>')
        .addClass("form-input__icon-item")
        .addClass("icon icon--exclamation-point")
        .addClass("bubble-icon bubble-icon--danger");
    return errorSpan;
}

function buildErrorList(errors) {
    var errorList = $("<ul></ul>");

    $(errors).each(function (index, element) {
        errorList.append($("<li></li>").text(element));
    });

    return errorList;
}

function validInputFile(inputName, inputId) {
    const fileInput = $('input[name="' + inputName + '"]');
    const fileInputContentError = $("input[id^=form_input_file_drag_and_drop_][id$=_with_text_label_" + inputId + "]");

    if (!fileInput.val()) {
        constructErrors(["Un fichier est requis"], fileInputContentError);
        return false;
    }
    return true;
}
