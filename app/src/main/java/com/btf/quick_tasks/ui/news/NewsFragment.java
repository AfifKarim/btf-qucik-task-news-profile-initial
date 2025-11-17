package com.btf.quick_tasks.ui.news;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btf.quick_tasks.R;
import com.btf.quick_tasks.appUtils.DatePickerFragment;
import com.btf.quick_tasks.appUtils.Global;
import com.btf.quick_tasks.dataBase.model.ArticleResponseModel;
import com.btf.quick_tasks.dataBase.model.NewsResponseModel;
import com.btf.quick_tasks.networkFile.ApiInterface;
import com.btf.quick_tasks.databinding.FragmentNewsBinding;
import com.btf.quick_tasks.networkFile.RetrofitApiCilent;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentNewsBinding binding;
    private ApiInterface apiInterface;
    private NewsAdapter newsAdapter;

    private ArrayAdapter<String> apiSP, paramsSP;
    private List<ArticleResponseModel> articleList = new ArrayList<>();
    private boolean ffrmDate, ftDate = false;
    private String selected_apiCall, selected_parameters, sfromDate, stoDate = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewsBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupSwipeRefresh();

        apiInterface = RetrofitApiCilent.getApiClient().create(ApiInterface.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinners();
        setupDatePickers();

        // Default values
        selected_apiCall = "everything";
        selected_parameters = "apple";
        sfromDate = Global.getCurrentMonth() + "-01";
        binding.fromDateTV.setText("From: " + sfromDate);
        stoDate = Global.getCurrentDateYYMMDD();
        binding.toDateTV.setText("To: " + stoDate);

        loadNews(); // Default load
    }

    private void setupSpinners() {
        // API spinner
        apiSP = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.select_apiCall));
        binding.apiSP.setAdapter(apiSP);

        binding.apiSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {   // "Select API"
                    selected_apiCall = null;
                    return;             // ❗ DO NOT CALL loadNews()
                }

                selected_apiCall = parent.getItemAtPosition(position).toString();

                // Update paramsSP based on selected_apiCall
                updateParamsSpinner(selected_apiCall);

                loadNews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Initial params spinner
        paramsSP = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.select_parameter1));
        binding.paramsSP.setAdapter(paramsSP);

        binding.paramsSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {  // "Select parameter"
                    selected_parameters = null;
                    return;            // ❗ DO NOT CALL loadNews()
                }

                selected_parameters = parent.getItemAtPosition(position).toString();

                if (selected_apiCall != null)
                    loadNews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDatePickers() {
        binding.fromDateTV.setOnClickListener(v -> {
            ffrmDate = true;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(NewsFragment.this, 0);
            datePicker.show(getFragmentManager(), "targetDate");
        });

        binding.toDateTV.setOnClickListener(v -> {
            ftDate = true;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(NewsFragment.this, 0);
            datePicker.show(getFragmentManager(), "targetDate");
        });
    }

    private void setupRecyclerView() {
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext(), articleList);
        binding.newsRecyclerView.setAdapter(newsAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadNews);
    }

    private void loadNews() {
        if (Global.isNetworkAvailable(requireContext())) {

            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            binding.noNewsPH.setVisibility(View.GONE);

            // SAFETY CHECK — prevents your crash
            if (!isAdded() || binding == null || selected_apiCall == null || selected_parameters == null) {
                Log.d("NewsAPI", "Skipping loadNews: required selection NULL");
                return;
            }
            binding.swipeRefreshLayout.setRefreshing(true);

            String q = null;
            String from = null;
            String to = null;
            String sortBy = null;
            String sources = null;
            String country = null;
            String category = null;

            // Setup parameters
            if ("everything".equals(selected_apiCall)) {
                switch (selected_parameters) {
                    case "apple":
                        q = "apple";
                        from = sfromDate;
                        to = stoDate;
                        sortBy = "popularity"; // sorted by popularity
                        break;
                    case "tesla":
                        q = "tesla";
                        from = sfromDate;
                        to = stoDate;
                        sortBy = "publishedAt"; // sorted by latest
                        break;
                    case "wsj.com":
                        sources = "wsj.com";
                        break;
                    default:
                        Log.e("NewsAPI", "Unknown parameter: " + selected_parameters);
                        binding.swipeRefreshLayout.setRefreshing(false);
                        return;
                }

                apiInterface.getEverything(q, from, to, sortBy, sources, Global.api_Key)
                        .enqueue(getNewsCallback(selected_parameters));
            } else if ("top-headlines".equals(selected_apiCall)) {
                switch (selected_parameters) {
                    case "country":
                        country = "us";
                        category = "business";
                        break;
                    case "sources":
                        sources = "techcrunch";
                        break;
                    default:
                        Log.e("NewsAPI", "Unknown parameter: " + selected_parameters);
                        binding.swipeRefreshLayout.setRefreshing(false);
                        return;
                }
                apiInterface.getTopHeadlines(country, category, sources, Global.api_Key)
                        .enqueue(getNewsCallback(selected_parameters));
            }

            // Log API request parameters
            Log.d("NewsAPI Request", "query=" + q + ", from=" + from + ", to=" + to +
                    ", sortBy=" + sortBy + ", sources=" + sources + ", country=" + country +
                    ", category=" + category + ", apiKey=" + Global.api_Key);

        } else {
            binding.swipeRefreshLayout.setVisibility(View.GONE);
            binding.noNewsPH.setVisibility(View.VISIBLE);
            Global.showDialog(requireContext(), R.drawable.ic_error, "Alert!", "You're not connected to the internet.");
        }
    }

    // Reusable callback for both API calls
    private Callback<NewsResponseModel> getNewsCallback(String selected_parameters) {
        return new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (!isAdded() || binding == null) return; // ✅ Safety check
                binding.swipeRefreshLayout.setRefreshing(false);

                try {
                    String rawResponse = response.isSuccessful() && response.body() != null
                            ? new Gson().toJson(response.body())
                            : (response.errorBody() != null ? response.errorBody().string() : "No response body");

                    Log.d("Raw Response", rawResponse);

                    if (response.isSuccessful() && response.body() != null) {
                        NewsResponseModel data = response.body();
                        binding.newsHeading.setText(selected_parameters + " news");

                        if ("ok".equals(data.getStatus()) && data.getArticles() != null && !data.getArticles().isEmpty()) {
                            articleList.clear();
                            articleList.addAll(data.getArticles());
                            newsAdapter.notifyDataSetChanged();
                        } else {
                            Global.showDialog(requireContext(), R.drawable.ic_error, data.getCode(), data.getMessage());
                        }
                    } else {
                        JSONObject obj = new JSONObject(rawResponse);
                        String code = obj.optString("code");
                        String message = obj.optString("message");
                        Global.showDialog(requireContext(), R.drawable.ic_error, code, message);
                        Log.e("NewsAPI Error", code + " - " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("NewsAPI", "Exception while reading response", e);
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {
                if (!isAdded() || binding == null) return;
                binding.swipeRefreshLayout.setRefreshing(false);
                Log.e("NewsAPI", "API call failed", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void updateParamsSpinner(String apiCall) {
        int arrayRes;
        if ("everything".equalsIgnoreCase(apiCall)) {
            binding.dateTitleLayout.setVisibility(View.VISIBLE);
            arrayRes = R.array.select_parameter1;
        } else {
            binding.dateTitleLayout.setVisibility(View.GONE);
            arrayRes = R.array.select_parameter2;
        }

        String[] paramsArray = getResources().getStringArray(arrayRes);

        paramsSP = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                paramsArray);
        binding.paramsSP.setAdapter(paramsSP);

        // Set previously selected value if it exists in the new array
        if (selected_parameters != null) {
            int pos = Arrays.asList(paramsArray).indexOf(selected_parameters);
            if (pos >= 0) {
                binding.paramsSP.setSelection(pos);
            } else {
                selected_parameters = null; // reset if not found
                binding.paramsSP.setSelection(0);
            }
        } else {
            binding.paramsSP.setSelection(0);
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (!isAdded() || binding == null) return;
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String selectedDate = sdf.format(c.getTime());

        if (ffrmDate) {
            stoDate = selectedDate;
            binding.fromDateTV.setText("From: " + Global.FormateDate(c.getTime()));
            ffrmDate = false;
            loadNews();
        }

        if (ftDate) {
            stoDate = selectedDate;
            binding.toDateTV.setText("To: " + Global.FormateDate(c.getTime()));
            ftDate = false;
            loadNews();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
