package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Note;

import org.jetbrains.annotations.NotNull;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "note_ingredient",
        primaryKeys = { "note_id", "name" },
        foreignKeys = { @ForeignKey(entity = Note.class,
                parentColumns = "id",
                childColumns = "note_id",
                onDelete = CASCADE)})
public class NoteIngredient {

    public NoteIngredient(int noteId, String name, Amount amount, boolean toUse) {
        this.noteId = noteId;
        this.name = name;
        this.amount = amount;
        this.toUse = toUse;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    @ColumnInfo(name="note_id")
    private int noteId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    private String name;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
    @Embedded
    private Amount amount;

    public boolean isToUse() {
        return toUse;
    }

    public void setToUse(boolean toUse) {
        this.toUse = toUse;
    }

    private boolean toUse;
}
