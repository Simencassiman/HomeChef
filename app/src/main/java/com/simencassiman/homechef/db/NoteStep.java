package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Note;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "note_step",
        primaryKeys = { "note_id", "step_nr" },
        foreignKeys = { @ForeignKey(entity = Note.class,
                parentColumns = "id",
                childColumns = "note_id",
                onDelete = CASCADE)})
public class NoteStep {


    public NoteStep(int noteId, int stepNr, String text) {
        this.noteId = noteId;
        this.stepNr = stepNr;
        this.text = text;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    @ColumnInfo(name = "note_id")
    private int noteId;

    public int getStepNr() {
        return stepNr;
    }

    public void setStepNr(int stepNr) {
        this.stepNr = stepNr;
    }

    @ColumnInfo(name = "step_nr")
    private int stepNr;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

}
