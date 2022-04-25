package com.zlagi.network.model

const val FEED_RESPONSE_JSON = """
{
  "pagination": {
    "total_count": 3,
    "current_page": 1,
    "total_pages": 1,
    "_links": {}
  },
  "results": [
    {
      "pk": 5788,
      "title": "test1",
      "description": "some random text for testing",
      "created": "Date: 2022-01-25 Time: 18:40:14",
      "updated": "Date: 2022-01-25 Time: 18:50:14",
      "username": "Zlagii"
    },
    {
      "pk": 5791,
      "title": "test2",
      "description": "some random text for testing",
      "created": "Date: 2022-01-25 Time: 18:40:14",
      "updated": "Date: 2022-01-25 Time: 18:59:12",
      "username": "Zlagii"
    },
    {
      "pk": 5792,
      "title": "test3",
      "description": "some random text for testing",
      "created": "Date: 2022-01-25 Time: 18:40:14",
      "updated": "Date: 2022-01-25 Time: 19:19:12",
      "username": "Frank"
    }
  ]
}
"""
