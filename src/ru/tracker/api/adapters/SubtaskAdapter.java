package ru.tracker.api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.tracker.model.Subtask;

import java.io.IOException;

public class SubtaskAdapter extends TypeAdapter<Subtask>  {
    @Override
    public void write(final JsonWriter jsonWriter, final Subtask subtask) throws IOException {
        if (subtask != null) {
            jsonWriter.value(subtask.toString());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Subtask read(final JsonReader jsonReader) throws IOException {
        return null; // пока просто как заглушка, нигде не используется
    }
}
