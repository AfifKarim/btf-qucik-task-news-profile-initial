package com.btf.quick_tasks.ui.tasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

public class TasksViewModel extends ViewModel {

    private final MutableLiveData<List<String>> texts = new MutableLiveData<>();

    public TasksViewModel() {
        texts.setValue(Arrays.asList(
                "Task One",
                "Task Two",
                "Task Three",
                "Task Four"
        ));
    }

    public LiveData<List<String>> getTexts() {
        return texts;
    }
}
