package com.tuanha.language.ui.fragment.subtitle.question.option.word.choose

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tuanha.coreapp.ui.base.adapters.MultiAdapter
import com.tuanha.coreapp.ui.base.fragments.BaseViewModelFragment
import com.tuanha.coreapp.utils.extentions.findParentFirstOrNull
import com.tuanha.coreapp.utils.extentions.findParentFirstOrThis
import com.tuanha.language.R
import com.tuanha.language.databinding.ComponentWordChooseBinding
import com.tuanha.language.ui.fragment.subtitle.SubtitleScope
import com.tuanha.language.ui.fragment.subtitle.SubtitleViewModel
import com.tuanha.language.ui.fragment.subtitle.question.option.OptionScope
import com.tuanha.language.ui.fragment.subtitle.question.option.word.choose.adapter.ChooseWordAdapter
import com.tuanha.language.utils.extention.doEnd
import com.tuanha.language.utils.extention.resumeActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel


class ChooseWordComponent : BaseViewModelFragment<ComponentWordChooseBinding, ChooseWordViewModel>(), OptionScope {


    private var chooseWordAdapter: MultiAdapter? = null


    private val subtitleViewModel: SubtitleViewModel by lazy {

        getKoin().getViewModel(findParentFirstOrThis<SubtitleScope>(), SubtitleViewModel::class)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTitle()
        setupRecyclerView()

        observeData()
        observeSubtitleData()
    }

    private fun setupTitle() {

        val binding = binding ?: return

        binding.tvTitle.setText(R.string.title_choose_word)
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val adapter = ChooseWordAdapter { view, chooseWordViewItem ->

            viewModel.updateOptionSelect(chooseWordViewItem)
        }

        chooseWordAdapter = MultiAdapter(adapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.layoutManager = CustomGridLayoutManager(requireContext())
        }
    }

    private fun observeData() = with(viewModel) {

        optionViewItemListDisplay.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            viewLifecycleOwner.lifecycleScope.launch(handler + Dispatchers.IO) {

                withContext(coroutineContext + Dispatchers.Main) {
                    binding.recyclerView.suppressLayout(false)
                }

                suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

                    launch(coroutineContext + Dispatchers.Main) {

                        (binding.recyclerView.layoutManager as CustomGridLayoutManager).run = Runnable {
                            cancellableContinuation.resumeActive(true)
                        }

                        chooseWordAdapter?.submitList(it)
                    }
                }

                withContext(coroutineContext + Dispatchers.Main) {
                    binding.recyclerView.suppressLayout(true)
                }

                if (it.any { it.data.isTrue }) suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

                    launch(coroutineContext + Dispatchers.Main) {
                        binding.lottieAnimationView.doEnd {
                            cancellableContinuation.resumeActive(true)
                        }.also { anim ->
                            anim.playAnimation()
                        }
                    }
                }

                if (it.any { it.data.isFalse }) suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

                    launch(coroutineContext + Dispatchers.Main) {
                        YoYo.with(Techniques.Tada).duration(300).repeat(1).doEnd {
                            cancellableContinuation.resumeActive(true)
                        }.also { anim ->
                            anim.playOn(binding.recyclerView)
                        }
                    }
                }

                if (it.any { it.data.isTrue }) {
                    (findParentFirstOrNull<SubtitleScope>() as SubtitleScope).next()
                } else if (it.any { it.data.isFalse }) {
                    viewModel.clearOption()
                }

            }
        }
    }

    private fun observeSubtitleData() = with(subtitleViewModel) {

        subtitleList.observe(viewLifecycleOwner) {

            viewModel.updateWords(it.flatMap { subtitle -> subtitle.words })
        }

        wordSelectAsync.observe(viewLifecycleOwner) {

            if (it != null) viewModel.updateWord(it)
        }

    }

    class CustomGridLayoutManager(context: Context?) : LinearLayoutManager(context) {

        var run: Runnable? = null

        override fun onLayoutCompleted(state: RecyclerView.State?) {
            super.onLayoutCompleted(state)
            run?.run()
        }

        override fun canScrollHorizontally(): Boolean {
            return false
        }

        override fun canScrollVertically(): Boolean {
            return false
        }
    }
}