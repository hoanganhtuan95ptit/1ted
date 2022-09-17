package com.tuanha.language.ui.fragment.subtitle

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuanha.core.utils.extentions.index
import com.tuanha.coreapp.ui.base.adapters.MultiAdapter
import com.tuanha.coreapp.ui.base.fragments.BaseViewModelFragment
import com.tuanha.coreapp.ui.servicer.BaseForegroundService
import com.tuanha.coreapp.utils.autoCleared
import com.tuanha.coreapp.utils.extentions.getOrDefault
import com.tuanha.coreapp.utils.extentions.getOrEmpty
import com.tuanha.coreapp.utils.extentions.getViewBy
import com.tuanha.language.BuildConfig
import com.tuanha.language.R
import com.tuanha.language.databinding.FragmentFirstBinding
import com.tuanha.language.entities.WordStatus
import com.tuanha.language.ui.fragment.subtitle.adapter.SpaceAdapter
import com.tuanha.language.ui.fragment.subtitle.adapter.SubtitleAdapter
import com.tuanha.language.ui.fragment.subtitle.adapter.SubtitleViewItem
import com.tuanha.language.ui.fragment.subtitle.question.option.OptionScope
import com.tuanha.language.ui.fragment.subtitle.question.option.word.choose.ChooseWordComponent
import com.tuanha.language.ui.service.SyncService
import com.tuanha.language.utils.extention.resumeActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine


class SubtitleFragment : BaseViewModelFragment<FragmentFirstBinding, SubtitleViewModel>(), SubtitleScope {

    private var mediaPlayer: MediaPlayer? = null

    private var subtitleAdapter by autoCleared<MultiAdapter>()


    val runnable: MediaRunnable by lazy {
        MediaRunnable(viewLifecycleOwner, viewModel, mediaPlayer!!, binding!!.seekbar)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (BuildConfig.DEBUG) binding?.root?.doOnLayout {
            BaseForegroundService.startOrResume(requireActivity().applicationContext, SyncService::class.java)
        }


        setupRecyclerView()

        binding?.seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                view.removeCallbacks(runnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                runnable.run(seekBar?.progress ?: 0)
            }
        })

        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun next() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.wordSelect?.status = WordStatus.True
            if (viewModel.subtitleSelect?.data?.words?.all { !it.isQuestionNotCompleted } == true) {

                runnable.run(viewModel.subtitleViewItemList.getOrEmpty().getOrNull(viewModel.subtitleIndex.getOrDefault(0) + 1)?.data?.start)
            } else {

                viewModel.choseWord(viewModel.subtitleSelect?.data?.words?.first { it.isQuestionNotCompleted })
            }
        }
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val adapter = SubtitleAdapter({ view, item ->

            runnable.run(item.data.start)
        }, { view, subtitleViewItem, word ->

//            if (subtitleViewItem.selected && word != null && word.isQuestionNotCompleted) {
//
//                viewModel.choseWord(word)
//            }
        })

        val spaceAdapter = SpaceAdapter()

        subtitleAdapter = MultiAdapter(adapter, spaceAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.layoutManager = CompletedLinearLayoutManager(binding.recyclerView.context)
        }

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener {

            binding.frameQuestion.translationY = binding.recyclerView.children.lastOrNull()?.bottom?.toFloat() ?: binding.recyclerView.bottom.toFloat()
        }

        binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {

            binding.frameQuestion.translationY = binding.recyclerView.children.lastOrNull()?.bottom?.toFloat() ?: binding.recyclerView.bottom.toFloat()
        }
    }

    private fun observeData() = with(viewModel) {

        wordSelectAsync.observe(viewLifecycleOwner) {

            val optionComponent = childFragmentManager.fragments.filterIsInstance<OptionScope>().firstOrNull() as? Fragment

            if (it != null) {

                childFragmentManager.beginTransaction().replace(R.id.frame_question, ChooseWordComponent()).commitNowAllowingStateLoss()
            } else if (optionComponent != null) {

                childFragmentManager.beginTransaction().remove(optionComponent).commitNowAllowingStateLoss()
            }
        }

        subtitleList.observe(viewLifecycleOwner) {

            mediaPlayer = mediaPlayer ?: MediaPlayer()

            mediaPlayer!!.setDataSource("https://raw.githubusercontent.com/hoanganhtuan95ptit/4Language/main/uEATpbQ9md4.mp3")
            mediaPlayer!!.setOnPreparedListener {
                binding?.seekbar?.max = it.duration
                mediaPlayer!!.start()
            }
            mediaPlayer!!.prepareAsync()

            view?.post(runnable)
        }

        subtitleSelectAsync.observe(viewLifecycleOwner) {


        }

        subtitleViewItemListDisplay.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            viewLifecycleOwner.lifecycleScope.launch(handler + Dispatchers.IO) {

                suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

                    launch(coroutineContext + Dispatchers.Main) {
                        (binding.recyclerView.layoutManager as CompletedLinearLayoutManager).run = Runnable {
                            cancellableContinuation.resumeActive(true)
                        }

                        subtitleAdapter?.submitList(it)
                    }
                }

                suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

                    launch(coroutineContext + Dispatchers.Main) {

                        val index = it.index { (it as? SubtitleViewItem)?.selected == true }

                        binding.recyclerView.smoothScrollTo(index) {
                            cancellableContinuation.resumeActive(true)
                        }
                    }
                }
            }
        }
    }

    private fun RecyclerView.smoothScrollTo(position: Int, onCompleted: () -> Unit = {}) {

        val childView = getViewBy(position)

        if (position < 0 || childView?.top == scrollY) {

            onCompleted.invoke()
            return
        }

        object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                removeOnScrollListener(this)

                if (childView != null) {

                    onCompleted.invoke()
                } else {
                    smoothScrollTo(position, onCompleted)
                }
            }
        }.apply {

            addOnScrollListener(this)
        }

        if (childView != null) {

            smoothScrollTo(childView)
        } else {

            scrollToPosition(position)
        }
    }

    private fun RecyclerView.smoothScrollTo(view: View) = view.post {

        var distance = view.top
        var viewParent = view.parent

        for (i in 0..9) {
            if ((viewParent as View) === this) break

            distance += (viewParent as View).top

            viewParent = viewParent.getParent()
        }

        distance -= (height - paddingBottom)

        smoothScrollBy(0, distance + view.height)
    }

    class MediaRunnable(val viewLifecycleOwner: LifecycleOwner, val viewModel: SubtitleViewModel, val mediaPlayer: MediaPlayer, val seekBar: SeekBar) : Runnable {

        override fun run() {
            run(null)
        }

        fun run(position: Int?) = viewLifecycleOwner.lifecycleScope.launch {

            if (position != null) {

                mediaPlayer.pause()
            }


            val currentPosition = position ?: mediaPlayer.currentPosition


            viewModel.updateSubtitleCurrent(currentPosition, position == null).join()


            if (viewModel.subtitleSelect != null && viewModel.subtitleSelect!!.data.words.any { it.isQuestionNotCompleted } && currentPosition in viewModel.subtitleSelect!!.data.end - 100..viewModel.subtitleSelect!!.data.end + 100) {

                mediaPlayer.pause()
                mediaPlayer.seekTo(viewModel.subtitleSelect!!.data.end)

                viewModel.choseWord(true).join()
            }


            seekBar.progress = currentPosition


            if (position != null) {

                mediaPlayer.seekTo(position)
                mediaPlayer.start()
            }


            seekBar.postDelayed(this@MediaRunnable, 5)
        }
    }

    open class CompletedLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {

        var run: Runnable? = null

        override fun onLayoutCompleted(state: RecyclerView.State?) {
            super.onLayoutCompleted(state)
            run?.run()
        }
    }
}

interface SubtitleScope {

    fun next()
}