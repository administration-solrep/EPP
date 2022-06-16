package fr.dila.ss.ui.bean.actions;

public class NoteEtapeActionDTO {
    private boolean isNoteAuthorOrHasSamePoste;

    private boolean isNoteAuthor;

    public NoteEtapeActionDTO() {
        // Default constructor
    }

    public boolean getIsNoteAuthorOrHasSamePoste() {
        return isNoteAuthorOrHasSamePoste;
    }

    public void setIsNoteAuthorOrHasSamePoste(boolean isNoteAuthorOrHasSamePoste) {
        this.isNoteAuthorOrHasSamePoste = isNoteAuthorOrHasSamePoste;
    }

    public boolean getIsNoteAuthor() {
        return isNoteAuthor;
    }

    public void setNoteAuthor(boolean isNoteAuthor) {
        this.isNoteAuthor = isNoteAuthor;
    }
}
