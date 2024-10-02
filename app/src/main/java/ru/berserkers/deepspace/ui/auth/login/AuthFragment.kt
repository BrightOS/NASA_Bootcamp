package ru.berserkers.deepspace.ui.auth.login


import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import ru.berserkers.deepspace.MainActivity
import ru.berserkers.deepspace.R
import ru.berserkers.deepspace.databinding.FragmentAuthBinding
import ru.berserkers.deepspace.utils.Data

/*
 * @author Vladimir Abubakirov
 */
@AndroidEntryPoint
class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels<AuthViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var loading = false

        binding.textName.doOnTextChanged { _, _, _, _ ->
            binding.textLayoutName.isErrorEnabled = false
        }
        binding.textPassword.doOnTextChanged { _, _, _, _ ->
            binding.textLayoutPassword.isErrorEnabled = false
        }

        binding.buttonLogin.setOnClickListener {
            if (!loading) {
                val userName = binding.textName.text.toString()
                val password = binding.textPassword.text.toString()

                if (userName.isEmpty() || password.isEmpty()) {
                    if (userName.isEmpty()) {
                        binding.textLayoutName.error = getString(R.string.emptyField)
                    }
                    if (password.isEmpty()) {
                        binding.textLayoutPassword.error = getString(R.string.emptyField)
                    }
                } else {
                    loading = true
                    (activity as MainActivity).apply {
                        startLoadingAnimation()
                        hideKeyboard()
                    }

                    lifecycleScope.launchWhenStarted {
                        when (val data =
                            viewModel.authenticateUser(requireContext(), userName, password)) {
                            is Data.Ok -> onSuccessLogin()
                            is Data.Error ->
                                (activity as MainActivity).showError(data.message)

                            is Data.Loading -> TODO("Show progress bar")
                            is Data.Local -> TODO()
                        }
                    }
                }
            }
        }

        binding.createAccount.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.reg())
        }

        super.onViewCreated(view, savedInstanceState)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onSuccessLogin() {
        (activity as MainActivity).apply {
            stopLoadingAnimation(true)
            changeHeader()
            hideKeyboard()
        }

        GlobalScope.launch {
            delay(1000)
            MainScope().launch {
                findNavController().navigate(R.id.success_login)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
