package ru.myitschool.nasa_bootcamp.ui.home.social_media

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.myitschool.nasa_bootcamp.data.model.*
import ru.myitschool.nasa_bootcamp.data.repository.NetworkRepository
import ru.myitschool.nasa_bootcamp.utils.Resource
import javax.inject.Inject

@HiltViewModel
class SocialMediaViewModelImpl @Inject constructor(private val networkRepository: NetworkRepository) :
    SocialMediaViewModel, ViewModel() {
    private val blogs =
        MutableLiveData<Resource<List<LiveData<ContentWithLikesAndComments<PostModel>>>>>()
    private val articles =
        MutableLiveData<Resource<List<LiveData<ContentWithLikesAndComments<ArticleModel>>>>>()
    private var selectedPost: ContentWithLikesAndComments<PostModel>? = null
    private var selectedArticle: ContentWithLikesAndComments<ArticleModel>? = null
    private val currentUser = MutableLiveData<UserModel?>(null)

    override fun getBlogs() = blogs
    override fun getNews() = articles

    override suspend fun loadBlogs() {
        blogs.postValue(networkRepository.getBlogPosts())
    }

    override suspend fun loadNews() {
        articles.postValue(networkRepository.getNews())
    }

    override suspend fun loadCurrentUser() {
        currentUser.postValue(networkRepository.getCurrentUser())
    }

    override fun getViewModelScope() = viewModelScope

    override fun setSelectedPost(post: ContentWithLikesAndComments<PostModel>) {
        selectedPost = post
    }

    override fun getSelectedPost() = selectedPost

    override fun setSelectedArticle(article: ContentWithLikesAndComments<ArticleModel>) {
        selectedArticle = article
    }

    override fun getSelectedArticle() = selectedArticle

    override suspend fun pressedLikeOnComment(
        item: ContentWithLikesAndComments<out Any>,
        comment: Comment
    ): Resource<Nothing> {
        return networkRepository.pressedLikeOnComment(item, comment)
    }

    override suspend fun pressedLikeOnItem(item: ContentWithLikesAndComments<out Any>): Resource<Nothing> {
        return networkRepository.pressedLikeOnItem(item)
    }

    override suspend fun sendMessage(
        message: String,
        id: Long,
        _class: Class<*>,
        parentComment: Comment?
    ): Resource<Nothing> {
        return networkRepository.sendComment(message, id, _class, parentComment)
    }

    override fun getCurrentUser() = currentUser
    override fun createPost(post: String, text: String, bitmap: Bitmap?) {
        TODO("Not yet implemented")
    }

}