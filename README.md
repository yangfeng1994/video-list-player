# video-list-player

使用RecyclerView+ExoPlayer 模仿抖音上下无缝滑动视频播放。

没有使用ViewPager2的原因是Fragment太重，内存消耗太大。

当有使用ViewPager2效果的时候，没有必要，可以选择这个方式，更轻量化。

有一点需要注意，在有键盘弹出的界面，RecyclerView的 onScrollStateChanged 监听方法，键盘弹出时，会被触发调用

demo中有配合使用ExoPlayer 进行视频上下分页播放


# 使用方法
第一步
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
第二步

```
dependencies {
	        implementation 'com.github.yangfeng1994:video-list-player:v1.0.1'
	}
```
第三步

```
val recyclerViewScrollerDetection = RecyclerViewScrollerDetection()
recyclerViewScrollerDetection.setCurrentListener(this)
recyclerViewScrollerDetection.attachToSnapHelper(snapHelper)
recyclerViewScrollerDetection.addOnScrollListener(mRecyclerView)
```

## setActive：当每次选中itemView的时候会调用
```
currentView: View?  当前选中的 itemView
position: Int,  当前选中itemView 对应的 position
previousView: View?  上一个选中的 itemView  第一次调用的时候，为null
previousPosition: Int 上一个选中的 itemView 对应的 position 第一次调用的时候为-1
```

## disActive：itemView 被移除屏幕的时候会调用，快速滑动的时候，可能会调用多次
```
previousView: View?  上一个选中的 itemView 
previousPosition: Int 上一个选中的 itemView 
```
## detachToSnapHelper：在需要移除监听的时候调用，防止内存泄漏
