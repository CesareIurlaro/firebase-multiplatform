package com.github.lamba92.firebasemultiplatform.storage

import com.google.firebase.FIRStorageTaskSnapshot
import com.google.firebase.FIRStorageTaskStatus
import com.google.firebase.FIRStorageUploadTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.callbackFlow

actual class UploadTask(
    val delegate: FIRStorageUploadTask
) : StorageTask<UploadTask.Snapshot> {

    override val isCanceled: Boolean
        get() = delegate.snapshot.status == FIRStorageTaskStatus.FIRStorageTaskStatusFailure
    override val isComplete: Boolean
        get() = delegate.snapshot.status == FIRStorageTaskStatus.FIRStorageTaskStatusSuccess
    override val isInProgress: Boolean
        get() = delegate.snapshot.status == FIRStorageTaskStatus.FIRStorageTaskStatusProgress
    override val isPaused: Boolean
        get() = delegate.snapshot.status == FIRStorageTaskStatus.FIRStorageTaskStatusPause
    override val isSuccessful: Boolean
        get() = delegate.snapshot.status == FIRStorageTaskStatus.FIRStorageTaskStatusSuccess

    init {

    }

    @ExperimentalCoroutinesApi
    override val progressFlow by lazy {
        callbackFlow {
            delegate.observeStatus(FIRStorageTaskStatus.FIRStorageTaskStatusProgress) {
                offer(it!!.toMppUploadTaskSnapshot())
            }
        }
    }

    @ExperimentalCoroutinesApi
    override val stateChangesFlow by lazy {
        delegate.statusFlow
    }

    override val snapshot: Snapshot
        get() = delegate.snapshot.toMppUploadTaskSnapshot()

    override fun cancel() =
        delegate.cancel()

    override fun pause() =
        delegate.pause()

    override fun resume() =
        delegate.resume()

    actual class Snapshot(val delegate: FIRStorageTaskSnapshot) : StorageTask.Snapshot {
        actual val metadata: StorageMetadata?
            get() = delegate.metadata?.toMpp()
        override val storage: StorageReference
            get() = delegate.reference.toMpp()
        override val bytesTransferred: Long
            get() = delegate.progress?.completedUnitCount ?: 0
        override val totalByteCount: Long
            get() = delegate.progress?.totalUnitCount ?: 0
    }

}
