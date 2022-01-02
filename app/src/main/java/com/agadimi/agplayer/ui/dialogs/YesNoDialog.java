package com.agadimi.agplayer.ui.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.agadimi.agplayer.databinding.DialogYesNoBinding;

public class YesNoDialog extends DialogFragment
{
    private DialogYesNoBinding binding;
    private OnYesNoListener listener;

    private String yes, no, message;

    public static YesNoDialog getInstance(String message, String yes, String no)
    {
        return new YesNoDialog(message, yes, no);
    }

    private YesNoDialog(String message, String yes, String no)
    {
        this.message = message;
        this.yes = yes;
        this.no = no;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DialogYesNoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.messageTv.setText(message);
        binding.yesBtn.setText(yes);
        binding.noBtn.setText(no);

        binding.yesBtn.setOnClickListener(v -> {
            if (listener != null) listener.onYesClick();
            dismiss();
        });

        binding.noBtn.setOnClickListener(v -> {
            if (listener != null) listener.onNoClick();
            dismiss();
        });
    }

    public YesNoDialog setListener(OnYesNoListener listener)
    {
        this.listener = listener;
        return this;
    }

    public interface OnYesNoListener
    {
        void onYesClick();

        void onNoClick();
    }
}
