import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useNavigate } from "react-router-dom";
import { Loader2, Plus, X } from "lucide-react";

const blogSchema = z.object({
  title: z
    .string()
    .min(1, "Title is required")
    .max(200, "Title must be less than 200 characters"),
  summary: z.string().optional(),
  content: z.string().min(1, "Content is required"),
});

const BlogForm = ({ initialData, onSubmit, isEditing = false }) => {
  const [imageUrls, setImageUrls] = useState(initialData?.imageUrls || []);
  const [newImageUrl, setNewImageUrl] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(blogSchema),
    defaultValues: {
      title: initialData?.title || "",
      summary: initialData?.summary || "",
      content: initialData?.content || "",
    },
  });

  const handleFormSubmit = async (data) => {
    setError("");
    setLoading(true);
    try {
      const blogData = {
        ...data,
        imageUrls: imageUrls.filter((url) => url.trim() !== ""),
      };
      await onSubmit(blogData);
      navigate("/blogs");
    } catch (err) {
      setError(
        err.response?.data?.message || "Failed to save blog. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const addImageUrl = () => {
    if (newImageUrl.trim() && !imageUrls.includes(newImageUrl.trim())) {
      setImageUrls([...imageUrls, newImageUrl.trim()]);
      setNewImageUrl("");
    }
  };

  const removeImageUrl = (index) => {
    setImageUrls(imageUrls.filter((_, i) => i !== index));
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="card p-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-6">
          {isEditing ? "Edit Blog Post" : "Create New Blog Post"}
        </h1>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          <div>
            <label htmlFor="title" className="label">
              Title *
            </label>
            <input
              {...register("title")}
              type="text"
              className="input"
              placeholder="Enter blog title"
            />
            {errors.title && (
              <p className="mt-1 text-sm text-red-600">
                {errors.title.message}
              </p>
            )}
          </div>

          <div>
            <label htmlFor="summary" className="label">
              Summary
            </label>
            <textarea
              {...register("summary")}
              rows={3}
              className="input"
              placeholder="Enter a brief summary of your blog post"
            />
            {errors.summary && (
              <p className="mt-1 text-sm text-red-600">
                {errors.summary.message}
              </p>
            )}
          </div>

          <div>
            <label htmlFor="content" className="label">
              Content *
            </label>
            <textarea
              {...register("content")}
              rows={12}
              className="input prose"
              placeholder="Write your blog content here..."
            />
            {errors.content && (
              <p className="mt-1 text-sm text-red-600">
                {errors.content.message}
              </p>
            )}
          </div>

          <div>
            <label className="label">Image URLs</label>
            <div className="space-y-3">
              {imageUrls.map((url, index) => (
                <div key={index} className="flex items-center space-x-2">
                  <input
                    type="url"
                    value={url}
                    onChange={(e) => {
                      const newUrls = [...imageUrls];
                      newUrls[index] = e.target.value;
                      setImageUrls(newUrls);
                    }}
                    className="input flex-1"
                    placeholder="Enter image URL"
                  />
                  <button
                    type="button"
                    onClick={() => removeImageUrl(index)}
                    className="p-2 text-red-600 hover:text-red-800"
                  >
                    <X size={16} />
                  </button>
                </div>
              ))}
              <div className="flex items-center space-x-2">
                <input
                  type="url"
                  value={newImageUrl}
                  onChange={(e) => setNewImageUrl(e.target.value)}
                  className="input flex-1"
                  placeholder="Enter image URL"
                />
                <button
                  type="button"
                  onClick={addImageUrl}
                  className="p-2 text-blue-600 hover:text-blue-800"
                >
                  <Plus size={16} />
                </button>
              </div>
            </div>
          </div>

          <div className="flex justify-end space-x-4">
            <button
              type="button"
              onClick={() => navigate("/blogs")}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button type="submit" disabled={loading} className="btn-primary">
              {loading ? (
                <>
                  <Loader2 className="animate-spin -ml-1 mr-2 h-4 w-4" />
                  {isEditing ? "Updating..." : "Creating..."}
                </>
              ) : isEditing ? (
                "Update Blog"
              ) : (
                "Create Blog"
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BlogForm;
