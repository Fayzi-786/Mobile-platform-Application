package com.fairshare.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.fairshare.app.databinding.FragmentForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private FirebaseAuth auth;

    public ForgotPasswordFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        NavController nav = Navigation.findNavController(view);

        binding.btnSendReset.setOnClickListener(v -> {
            String email = String.valueOf(binding.editEmail.getText()).trim();
            if (TextUtils.isEmpty(email)) { binding.editEmail.setError("Required"); return; }
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(),"Reset email sent.",Toast.LENGTH_SHORT).show();
                    nav.navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
                } else {
                    Toast.makeText(requireContext(),
                            "Failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.linkBackToSignIn.setOnClickListener(v ->
                nav.navigate(R.id.action_forgotPasswordFragment_to_signInFragment));
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
