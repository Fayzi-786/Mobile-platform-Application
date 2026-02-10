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

import com.fairshare.app.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseAuth auth;

    public RegisterFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        NavController nav = Navigation.findNavController(view);

        binding.btnCreateAccount.setOnClickListener(v -> {
            String name = String.valueOf(binding.editName.getText()).trim();
            String email = String.valueOf(binding.editEmail.getText()).trim();
            String pass  = String.valueOf(binding.editPassword.getText());
            String conf  = String.valueOf(binding.editConfirm.getText());

            if (!binding.cbTerms.isChecked()) {
                Toast.makeText(requireContext(), "Please accept terms.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) { binding.editEmail.setError("Required"); return; }
            if (TextUtils.isEmpty(pass))  { binding.editPassword.setError("Required"); return; }
            if (!pass.equals(conf))       { binding.editConfirm.setError("Passwords must match"); return; }

            binding.btnCreateAccount.setEnabled(false);

            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity(), (Task<AuthResult> task) -> {
                        binding.btnCreateAccount.setEnabled(true);
                        if (task.isSuccessful()) {
                            if (!TextUtils.isEmpty(name) && auth.getCurrentUser() != null) {
                                auth.getCurrentUser().updateProfile(
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build());
                            }
                            nav.navigate(R.id.action_registerFragment_to_homeFragment);
                        } else {
                            Toast.makeText(requireContext(),
                                    "Registration failed: " + (task.getException() != null
                                            ? task.getException().getMessage() : "unknown error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.linkToSignIn.setOnClickListener(
                v -> nav.navigate(R.id.action_registerFragment_to_signInFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
