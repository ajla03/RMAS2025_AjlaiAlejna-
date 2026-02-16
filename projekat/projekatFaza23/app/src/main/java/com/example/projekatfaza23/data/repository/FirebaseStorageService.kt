import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import java.util.UUID

object FirebaseStorageService {

    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadFile(context: Context, uri: Uri, fileName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val uniqueName = "${UUID.randomUUID()}_${fileName}"
                val storageRef = storage.reference
                    .child("leave_attachments/$uniqueName")

                storageRef.putFile(uri).await()

                val downloadUrl = storageRef.downloadUrl.await().toString()

                Result.success(downloadUrl)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}