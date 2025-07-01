package ru.tracker.api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.tracker.model.Epic;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic>  {
    @Override
    public void write(final JsonWriter jsonWriter, final Epic epic) throws IOException {
        if (epic != null) {
            jsonWriter.value(epic.toString());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Epic read(final JsonReader jsonReader) throws IOException {
        return null; // пока просто как заглушка, нигде не используется
    }
}
